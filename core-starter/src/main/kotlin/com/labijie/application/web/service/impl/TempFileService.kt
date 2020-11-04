package com.labijie.application.web.service.impl

import com.labijie.application.component.IObjectStorage
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.web.data.domain.TempFile
import com.labijie.application.web.data.domain.TempFileExample
import com.labijie.application.web.data.mapper.TempFileMapper
import com.labijie.application.web.service.ITempFileService
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.logger
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
@Service
class TempFileService(
    private val objectStorage: IObjectStorage,
    private val idGenerator: IIdGenerator,
    private val tempFileMapper: TempFileMapper
) : ITempFileService {

    @Transactional
    override fun deleteFiles(filePath: List<String>, deleteObject: Boolean): Int {
        if(filePath.isEmpty()){
            return 0
        }
        if (deleteObject) {
            filePath.forEach {
                objectStorage.deleteObject(it)
            }
        }

        val example = TempFileExample().apply {
            this.createCriteria().andPathIn(filePath)
        }
       return tempFileMapper.deleteByExample(example)
    }

    @Transactional
    override fun deleteFile(filePath: String, deleteObject: Boolean): Boolean {
        if (deleteObject) {
            objectStorage.deleteObject(filePath)
        }

        val example = TempFileExample().apply {
            this.createCriteria().andPathEqualTo(filePath)
        }
        val file = tempFileMapper.selectByExample(example).firstOrNull()
        if(file != null){
            val count = tempFileMapper.deleteByPrimaryKey(file.id)
            return count == 1
        }
        return false;
    }

    override fun checkFile(filePath: String) {
        if (filePath.isBlank()) {
            throw StoredObjectNotFoundException()
        }
        objectStorage.existObject(filePath, true)
    }

    @Transactional
    override fun saveTempFile(filePath: String, fileType: String): Boolean {
        val id = idGenerator.newId()
        val file = TempFile().apply {
            this.id = id
            this.path = filePath
            this.timeCreated = System.currentTimeMillis()
            this.fileType = fileType
        }
        return try {
            val count =  tempFileMapper.insert(file)
            count > 0
        } catch (e: DuplicateKeyException) {
            logger.warn("The temp file '$file' you want to store already existed and detects that a duplicate save operation was detected.")
            false
        }
    }
}