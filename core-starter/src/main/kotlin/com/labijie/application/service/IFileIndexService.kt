/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
package com.labijie.application.service

import com.labijie.application.data.pojo.FileIndex
import com.labijie.application.model.FileModifier


interface IFileIndexService {

    companion object {
        const val TEMP_FILE_TYPE = "temp"
    }

    fun touchFile(filePath: String, modifier: FileModifier): FileIndex
    fun saveFile(filePath: String, fileType:String, entityId: Long? = null): FileIndex?
    fun checkFileInStorage(filePath:String, throwIfNotStored: Boolean) : Boolean
    fun deleteFile(filePath: String, deleteObject:Boolean = false):Boolean
    fun deleteFiles(filePaths: List<String>, deleteObject: Boolean = false): Int
}