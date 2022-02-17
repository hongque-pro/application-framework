package com.labijie.application.service.impl

import com.labijie.application.data.domain.FileIndex
import com.labijie.application.data.domain.TempFileExample
import com.labijie.application.data.mapper.FileIndexMapper
import com.labijie.application.data.mapper.TempFileMapper
import com.labijie.application.propertiesFrom
import com.labijie.application.service.IFileIndexService
import com.labijie.application.service.ITempFileService
import com.labijie.infra.utils.logger
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FileIndexService(
  private val tempFileService: ITempFileService,
  private val tempFileMapper: TempFileMapper,
  private val fileIndexMapper: FileIndexMapper,
) : IFileIndexService {

  @Transactional
  override fun saveFileFromTemp(filePath: String, fileType: String, entityId: Long, deleteTemp: Boolean): Boolean {
    tempFileService.checkFile(filePath)
    val temp = tempFileMapper.selectByExample(
      TempFileExample().apply {
        this.createCriteria()
          .andPathEqualTo(filePath)
      }
    ).firstOrNull() ?: return false
    val file = FileIndex().propertiesFrom(temp).apply {
      this.entityId = entityId
    }
    if (deleteTemp) {
      tempFileMapper.deleteByPrimaryKey(temp.id)
    }
    return try {
      fileIndexMapper.insert(file) > 0
    } catch (e: DuplicateKeyException) {
      logger.warn("The file index '$file' you want to store already existed and detects that a duplicate save operation was detected.")
      false
    }
  }
}