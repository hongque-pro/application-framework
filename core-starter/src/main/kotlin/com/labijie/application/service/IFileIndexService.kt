/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
package com.labijie.application.service

import com.labijie.application.data.pojo.FileIndex
import com.labijie.application.model.FileModifier
import com.labijie.application.model.ObjectPreSignUrl


interface IFileIndexService {

    companion object {
        const val TEMP_FILE_TYPE = "temp"
    }

    fun getIndex(filePath: String): FileIndex?

    fun getIndexes(filePaths: Iterable<String>): Map<String, FileIndex?>

    fun getIndexById(fileIndexId: Long): FileIndex?

    fun getFileUrl(filePath: String, modifier: FileModifier): ObjectPreSignUrl
    fun touchFile(filePath: String, modifier: FileModifier, fileSizeInBytes: Long? = null): TouchedFile

    fun setToTemp(filePath: String, throwIfNotStored: Boolean): FileIndex?
    fun saveFile(filePath: String, fileType:String,  entityId: Long? = null, fileSizeInBytes: Long? = null, checkFileExisted: Boolean = true): FileIndex?
    fun checkFileInStorage(filePath:String, throwIfNotStored: Boolean) : Boolean
    fun deleteFile(filePath: String, deleteObject:Boolean = false):Boolean
    fun deleteFiles(filePaths: List<String>, deleteObject: Boolean = false): Int
    fun copyFile(sourceFilePath: String, destFilePath: String, destModifier: FileModifier? = null, destFileType: String? = null, destEntityId: Long? = null): FileIndex
}
