package com.labijie.application.service.impl

import com.labijie.application.*
import com.labijie.application.component.GenerationURLPurpose
import com.labijie.application.component.IObjectStorage
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.data.FileIndexTable
import com.labijie.application.data.TempFileIndexTable
import com.labijie.application.data.pojo.FileIndex
import com.labijie.application.data.pojo.TempFileIndex
import com.labijie.application.data.pojo.dsl.FileIndexDSL.deleteByPrimaryKey
import com.labijie.application.data.pojo.dsl.FileIndexDSL.insert
import com.labijie.application.data.pojo.dsl.FileIndexDSL.selectByPrimaryKey
import com.labijie.application.data.pojo.dsl.FileIndexDSL.selectMany
import com.labijie.application.data.pojo.dsl.FileIndexDSL.selectOne
import com.labijie.application.data.pojo.dsl.FileIndexDSL.updateByPrimaryKey
import com.labijie.application.data.pojo.dsl.TempFileIndexDSL.deleteByPrimaryKey
import com.labijie.application.data.pojo.dsl.TempFileIndexDSL.insert
import com.labijie.application.data.pojo.dsl.TempFileIndexDSL.selectByPrimaryKey
import com.labijie.application.data.pojo.dsl.TempFileIndexDSL.selectForward
import com.labijie.application.exception.FileIndexAlreadyExistedException
import com.labijie.application.exception.FileIndexNotFoundException
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.exception.TemporaryFileTimoutException
import com.labijie.application.model.FileModifier
import com.labijie.application.model.ObjectPreSignUrl
import com.labijie.application.model.toObjectBucket
import com.labijie.application.service.*
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.throwIfNecessary
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import kotlin.io.path.Path
import kotlin.io.path.extension

/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
class FileIndexService(
    private val properties: ApplicationCoreProperties,
    private val transactionTemplate: TransactionTemplate,
    private val idGenerator: IIdGenerator,
    private val objectStorage: IObjectStorage
) : IFileIndexService {

    companion object {
        private val logger = LoggerFactory.getLogger(FileIndexService::class.java)
    }

    override fun getFileUrl(filePath: String, modifier: FileModifier): ObjectPreSignUrl {
        if (filePath.isBlank()) {
            throw ApplicationRuntimeException("File path can not be null or empty string.")
        }
        val bucket = if (modifier == FileModifier.Public) BucketPolicy.PUBLIC else BucketPolicy.PRIVATE
        return objectStorage.generateObjectUrl(filePath, bucket)
    }

    override fun getIndexes(filePaths: Iterable<String>): Map<String, FileIndex?> {
        val files = transactionTemplate.executeReadOnly {
            FileIndexTable.selectMany {
                FileIndexTable.path.inList(filePaths)
            }
        } ?: listOf()

        val map = mutableMapOf<String, FileIndex?>()
        filePaths.forEach {
            map[it] = files.find { f -> f.path.equals(it, ignoreCase = true) }
        }
        return map
    }

    override fun touchFile(
        filePath: String,
        modifier: FileModifier,
        fileSizeInBytes: Long?,
        expiration: Duration?
    ): TouchedFile {
        if (filePath.isBlank()) {
            throw IllegalArgumentException("File path can not be null or empty string when touch file.")
        }

        val fileIndex = transactionTemplate.execute {
            val file = FileIndexTable.selectOne(FileIndexTable.id) {
                andWhere { FileIndexTable.path eq filePath }
            }
            if (file != null) {
                throw FileIndexAlreadyExistedException(filePath)
            }

            val index = FileIndex().apply {
                this.id = idGenerator.newId()
                this.path = filePath
                this.fileType = IFileIndexService.TEMP_FILE_TYPE
                this.timeCreated = System.currentTimeMillis()
                this.fileAccess = modifier
                this.sizeIntBytes = fileSizeInBytes ?: 0

            }

            val tempIndex = TempFileIndex().propertiesFrom(index).apply {
                this.setTimeout(expiration ?: properties.file.tempFileExpiration)
            }

            FileIndexTable.insert(index)
            TempFileIndexTable.insert(tempIndex)
            index

        } ?: throw ApplicationRuntimeException("A database error has occurred while touching file.")

        val bucketPolicy = if (modifier == FileModifier.Public) BucketPolicy.PUBLIC else BucketPolicy.PRIVATE
        val url = objectStorage.generateObjectUrl(filePath, bucketPolicy, GenerationURLPurpose.Write)
        return TouchedFile(
            fileIndexId = fileIndex.id,
            filePath = filePath,
            uploadUrl = url.url,
            mime = MimeUtils.getMimeByExtensions(Path(filePath).extension),
            timeoutMills = url.timeoutMills
        )
    }

    override fun saveFile(
        filePath: String,
        fileType: String,
        entityId: Long?,
        fileSizeInBytes: Long?,
        checkFileExisted: Boolean
    ): FileIndex? {

        val existedFile = transactionTemplate.executeReadOnly {
            FileIndexTable.selectOne {
                andWhere { FileIndexTable.path eq filePath }
            }
        }

        if (existedFile == null) {
            if (checkFileExisted) {
                throw FileIndexNotFoundException(filePath)
            }
            return null
        }

        if(fileType == existedFile.fileType) {
            logger.warn("Save file use the origin file type ( current type: ${existedFile.fileType}, save type: $fileType )")
            return existedFile
        }

        val wantToGetFileSize = (fileSizeInBytes == null && existedFile.sizeIntBytes <= 0)
        //如果后面需要从获取文件大小，这里可以少一次检查， 因为 getObjectSizeInBytes 会检查文件是否存在
        if (checkFileExisted && !wantToGetFileSize) {
            checkFileInStorage(filePath, true)
        }

        val temp = TempFileIndexTable.selectByPrimaryKey(existedFile.id)


        if(temp?.isExpired() == true) throw TemporaryFileTimoutException()

        val sizeToUpdate = if(existedFile.sizeIntBytes <= 0) {
            val sizeInBytes =
                fileSizeInBytes ?: objectStorage.getObjectSizeInBytes(filePath, existedFile.fileAccess.toObjectBucket())
            if (sizeInBytes == null && checkFileExisted) { //前面跳过了检查，这里补回来
                throw StoredObjectNotFoundException()
            }
            sizeInBytes
        } else null

        return transactionTemplate.execute {
            if (!existedFile.isTempFile()) {
                throw FileIndexAlreadyExistedException(filePath)
            }

            existedFile.fileType = fileType
            existedFile.entityId = entityId ?: 0
            existedFile.timeCreated = System.currentTimeMillis()
            sizeToUpdate?.let {
                existedFile.sizeIntBytes = sizeToUpdate
            }

            val columns = if(sizeToUpdate != null) {
                arrayOf<Column<*>>(
                    FileIndexTable.fileType,
                    FileIndexTable.entityId,
                    FileIndexTable.timeCreated,
                    FileIndexTable.sizeIntBytes)
            }else {
                arrayOf<Column<*>>(
                    FileIndexTable.fileType,
                    FileIndexTable.entityId,
                    FileIndexTable.timeCreated)
            }
            temp?.let {
                TempFileIndexTable.deleteByPrimaryKey(temp.id)
            }
            val count = FileIndexTable.updateByPrimaryKey(existedFile, *columns)
            if (count > 0) existedFile else null
        }
    }

    override fun setToTemp(filePath: String, throwIfNotStored: Boolean): FileIndex? {
        return transactionTemplate.execute {

            val existedFile = FileIndexTable.selectOne {
                andWhere { FileIndexTable.path eq filePath }
            }
            if (existedFile == null) {
                if (throwIfNotStored) {
                    throw FileIndexNotFoundException(filePath)
                }
                null
            } else {
                if(!existedFile.fileType.equals(IFileIndexService.TEMP_FILE_TYPE, ignoreCase = true)) {

                    existedFile.fileType = IFileIndexService.TEMP_FILE_TYPE
                    existedFile.entityId = 0

                    val count =
                        FileIndexTable.updateByPrimaryKey(existedFile, FileIndexTable.fileType, FileIndexTable.entityId)
                    if(count > 0 ){
                        val tempIndex = TempFileIndex().propertiesFrom(existedFile).apply {
                            this.setTimeout(properties.file.tempFileExpiration)
                        }
                        TempFileIndexTable.insert(tempIndex)
                    }
                }
                existedFile
            }
        }
    }

    override fun checkFileInStorage(filePath: String, throwIfNotStored: Boolean): Boolean {
        if (filePath.isBlank()) {
            if (throwIfNotStored) {
                throw StoredObjectNotFoundException()
            }
            return false
        }

        val file = transactionTemplate.executeReadOnly {
            FileIndexTable.selectOne {
                andWhere { FileIndexTable.path eq filePath }
            }
        }

        if (file == null) {
            if (throwIfNotStored) {
                throw FileIndexNotFoundException(filePath)
            }
            return false
        }

        val bucket = if (file.fileAccess == FileModifier.Private) BucketPolicy.PRIVATE else BucketPolicy.PUBLIC
        return objectStorage.existObject(filePath, throwIfNotStored, bucket)
    }

    override fun deleteFile(filePath: String, deleteObject: Boolean): Boolean {
        if (deleteObject) {
            objectStorage.deleteObject(filePath)
        }
        return transactionTemplate.execute {
            val file = FileIndexTable.selectOne(FileIndexTable.id, FileIndexTable.fileType) {
                andWhere { FileIndexTable.path eq filePath }
            }
            file?.let {
                val deleted = FileIndexTable.deleteByPrimaryKey(file.id) == 1
                if(deleted && file.isTempFile()) {
                    TempFileIndexTable.deleteByPrimaryKey(file.id)
                }
                deleted
            } ?: false
        } ?: false
    }

    override fun deleteFiles(filePaths: List<String>, deleteObject: Boolean): Int {
        if (filePaths.isEmpty()) {
            return 0
        }
        if (deleteObject) {
            filePaths.forEach {
                objectStorage.deleteObject(it)
            }
        }

        return transactionTemplate.execute {

            val fileIds = FileIndexTable.selectMany(FileIndexTable.path, FileIndexTable.fileType) {
                andWhere { FileIndexTable.path inList filePaths }
            }

            val count = FileIndexTable.deleteWhere {
                FileIndexTable.id inList fileIds.map { it.id }
            }
            if(count > 0) {
               val tempIds = fileIds.filter { it.isTempFile() }.map { it.id }
                TempFileIndexTable.deleteWhere { TempFileIndexTable.id inList tempIds }
            }
            count

        } ?: 0
    }

    override fun getIndex(filePath: String): FileIndex? {
        return transactionTemplate.executeReadOnly {
            FileIndexTable.selectOne {
                andWhere { FileIndexTable.path.eq(filePath) }
            }
        }
    }

    override fun getIndexById(fileIndexId: Long): FileIndex? {
        return transactionTemplate.executeReadOnly {
            FileIndexTable.selectByPrimaryKey(fileIndexId)
        }
    }

    override fun copyFile(
        sourceFilePath: String,
        destFilePath: String,
        destModifier: FileModifier?,
        destFileType: String?,
        destEntityId: Long?
    ): FileIndex {
        val index = getIndex(sourceFilePath) ?: throw FileIndexNotFoundException(sourceFilePath)
        val sourceBucket = if (index.fileAccess === FileModifier.Private) BucketPolicy.PRIVATE else BucketPolicy.PUBLIC
        val destModifierValue = destModifier ?: index.fileAccess
        val destBucket = if (destModifierValue === FileModifier.Private) BucketPolicy.PRIVATE else BucketPolicy.PUBLIC
        val dest = getIndex(destFilePath)
        if (dest != null) {
            throw FileIndexAlreadyExistedException(dest.path)
        }
        objectStorage.copyObject(index.path, sourceBucket, destFilePath, destBucket)
        return try {
            transactionTemplate.execute {
                val file = FileIndex().apply {
                    id = idGenerator.newId()
                    path = destFilePath
                    fileType = destFileType ?: IFileIndexService.TEMP_FILE_TYPE
                    entityId = destEntityId ?: 0
                    fileAccess = destModifierValue
                    timeCreated = System.currentTimeMillis()
                    sizeIntBytes = index.sizeIntBytes
                }
                FileIndexTable.insert(file)
                file
            }!!
        } catch (e: Throwable) {
            objectStorage.deleteObject(destFilePath, destBucket)
            throw e
        }
    }

    override fun clearTempFiles(durationAfterExpired: Duration, batchSize: Int, throwIfError: Boolean): Int {
        var deletedCount = 0
        try {
            var list = this.transactionTemplate.executeReadOnly(propagation = Propagation.REQUIRES_NEW) {
                TempFileIndexTable.selectForward(
                    TempFileIndexTable.timeExpired,
                    order = SortOrder.ASC,
                    pageSize = batchSize
                )
            }!!

            while (list.list.isNotEmpty()) {
                val canDelete = list.list.filter {
                    it.isSafelyForDelete(durationAfterExpired)
                }

                val ids = canDelete.map { it.id }

                FileIndexTable.deleteWhere { FileIndexTable.id inList ids }
                TempFileIndexTable.deleteWhere { TempFileIndexTable.id inList ids }
                canDelete.forEach {
                    objectStorage.deleteObject(it.path, it.fileAccess.toObjectBucket())
                }
                deletedCount += canDelete.size

                val token = list.forwardToken
                if(token.isNullOrBlank()) {
                    break
                }

                list = this.transactionTemplate.executeReadOnly(propagation = Propagation.REQUIRES_NEW) {
                    TempFileIndexTable.selectForward(
                        TempFileIndexTable.timeExpired,
                        order = SortOrder.ASC,
                        pageSize = batchSize,
                        forwardToken = token
                    )
                }!!
            }
        } catch (e: Throwable) {
            e.throwIfNecessary()
            if(!throwIfError) {
                throw e
            }else {
                logger.error("Error while clear temp files.", e)
            }
        }
        return deletedCount
    }
}