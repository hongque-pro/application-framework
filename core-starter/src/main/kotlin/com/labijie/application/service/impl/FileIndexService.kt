package com.labijie.application.service.impl

import com.labijie.application.ApplicationRuntimeException
import com.labijie.application.BucketPolicy
import com.labijie.application.MimeUtils
import com.labijie.application.component.GenerationURLPurpose
import com.labijie.application.component.IObjectStorage
import com.labijie.application.data.FileIndexTable
import com.labijie.application.data.pojo.FileIndex
import com.labijie.application.data.pojo.dsl.FileIndexDSL.deleteByPrimaryKey
import com.labijie.application.data.pojo.dsl.FileIndexDSL.insert
import com.labijie.application.data.pojo.dsl.FileIndexDSL.selectMany
import com.labijie.application.data.pojo.dsl.FileIndexDSL.selectOne
import com.labijie.application.data.pojo.dsl.FileIndexDSL.updateByPrimaryKey
import com.labijie.application.exception.FileIndexAlreadyExistedException
import com.labijie.application.exception.FileIndexNotFoundException
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.model.FileModifier
import com.labijie.application.model.ObjectPreSignUrl
import com.labijie.application.service.IFileIndexService
import com.labijie.application.service.TouchedFile
import com.labijie.infra.IIdGenerator
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.springframework.transaction.support.TransactionTemplate
import kotlin.io.path.Path
import kotlin.io.path.extension

/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
class FileIndexService(
    private val transactionTemplate: TransactionTemplate,
    private val idGenerator: IIdGenerator,
    private val objectStorage: IObjectStorage) : IFileIndexService {

    override fun getFileUrl(filePath: String, modifier: FileModifier): ObjectPreSignUrl {
        if(filePath.isBlank()) {
            throw ApplicationRuntimeException("File path can not be null or empty string.")
        }
        val bucket = if(modifier == FileModifier.Public) BucketPolicy.PUBLIC else BucketPolicy.PRIVATE
        return objectStorage.generateObjectUrl(filePath,  bucket)
    }

    override fun touchFile(filePath: String, modifier: FileModifier): TouchedFile {
        if(filePath.isBlank()) {
            throw ApplicationRuntimeException("File path can not be null or empty string.")
        }

        val fileIndex = transactionTemplate.execute {
            val file = FileIndexTable.selectOne(FileIndexTable.id) {
                andWhere { FileIndexTable.path eq filePath }
            }
            if (file != null) {
                throw FileIndexAlreadyExistedException(filePath)
            }

            FileIndex().apply {
                this.id = idGenerator.newId()
                this.path = filePath
                this.fileType = IFileIndexService.TEMP_FILE_TYPE
                this.timeCreated = System.currentTimeMillis()
                this.fileAccess = modifier
                FileIndexTable.insert(this)
            }
        } ?: throw ApplicationRuntimeException("A database error has occurred while touching file.")

        val bucketPolicy = if(modifier == FileModifier.Public) BucketPolicy.PUBLIC else BucketPolicy.PRIVATE
        val url = objectStorage.generateObjectUrl(filePath, bucketPolicy, GenerationURLPurpose.Write)
        return TouchedFile(
            fileIndexId =  fileIndex.id,
            filePath = filePath,
            uploadUrl = url.url,
            mime = MimeUtils.getMimeByExtensions(Path(filePath).extension),
            timeoutMills = url.timeoutMills)
    }

    override fun saveFile(filePath: String, fileType: String, entityId: Long?): FileIndex? {

        checkFileInStorage(filePath, true)

        return transactionTemplate.execute {

            val existedFile = FileIndexTable.selectOne {
                andWhere { FileIndexTable.path eq filePath }
            }
            if(existedFile == null){
                val id = idGenerator.newId()
                val file = FileIndex().apply {
                    this.id = id
                    this.path = filePath
                    this.timeCreated = System.currentTimeMillis()
                    this.fileType = fileType
                    this.entityId = entityId ?: 0
                }
                FileIndexTable.insert(file)
                file
            }else {
                if(existedFile.fileType.lowercase() != IFileIndexService.TEMP_FILE_TYPE.lowercase()) {
                    throw FileIndexAlreadyExistedException(filePath)
                }
                existedFile.fileType = fileType
                existedFile.entityId = entityId ?: 0
                existedFile.timeCreated = System.currentTimeMillis()
                val count = FileIndexTable.updateByPrimaryKey(existedFile)
                if(count > 0) existedFile else null
            }
        }
    }

    override fun setToTemp(filePath: String, throwIfNotStored: Boolean): Boolean {
        return transactionTemplate.execute {

            val existedFile = FileIndexTable.selectOne {
                andWhere { FileIndexTable.path eq filePath }
            }
            if(existedFile == null){
                if(throwIfNotStored) {
                    throw FileIndexNotFoundException(filePath)
                }
                false
            }else {
                existedFile.fileType = IFileIndexService.TEMP_FILE_TYPE
                existedFile.entityId = 0
                existedFile.timeCreated = System.currentTimeMillis()
                val count = FileIndexTable.updateByPrimaryKey(existedFile)
                count > 0
            }
        } ?: false
    }

    override fun checkFileInStorage(filePath: String, throwIfNotStored: Boolean): Boolean {
        if (filePath.isBlank()) {
            if(throwIfNotStored) {
                throw StoredObjectNotFoundException()
            }
            return false
        }
        return objectStorage.existObject(filePath, throwIfNotStored)
    }

    override fun deleteFile(filePath: String, deleteObject: Boolean): Boolean {
        if (deleteObject) {
            objectStorage.deleteObject(filePath)
        }
        return transactionTemplate.execute {
            val file = FileIndexTable.selectOne(FileIndexTable.id) {
                andWhere { FileIndexTable.path eq filePath }
            }
            file?.let {
                FileIndexTable.deleteByPrimaryKey(file.id) == 1
            } ?: false
        } ?: false
    }

    override fun deleteFiles(filePaths: List<String>, deleteObject: Boolean): Int {
        if(filePaths.isEmpty()){
            return 0
        }
        if (deleteObject) {
            filePaths.forEach {
                objectStorage.deleteObject(it)
            }
        }

        return transactionTemplate.execute {

            val fileIds = FileIndexTable.selectMany(FileIndexTable.path) {
                andWhere { FileIndexTable.path inList filePaths }
            }.map { it.id }

            FileIndexTable.deleteWhere {
                FileIndexTable.id inList fileIds
            }
        } ?: 0
    }
}