package com.labijie.application.service

interface IFileIndexService {
  fun saveFileFromTemp(filePath: String, fileType: String, entityId: Long = 0, deleteTemp: Boolean = true): Boolean
}