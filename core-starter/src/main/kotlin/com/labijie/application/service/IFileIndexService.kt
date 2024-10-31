/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
package com.labijie.application.service

import com.labijie.application.data.pojo.FileIndex
import com.labijie.application.data.pojo.TempFileIndex
import com.labijie.application.model.FileModifier
import com.labijie.application.model.ObjectPreSignUrl
import java.io.InputStream
import java.time.Duration


interface IFileIndexService {

    companion object {
        const val TEMP_FILE_TYPE = "temp"
    }

    fun getIndex(filePath: String): FileIndex?

    fun getIndexAndRefreshFileSize(filePath: String, checkFileExisted: Boolean = true): FileIndex?

    fun existed(filePath: String): Boolean

    fun getIndexes(filePaths: Iterable<String>): Map<String, FileIndex?>

    fun getIndexById(fileIndexId: Long): FileIndex?

    fun getFileUrl(filePath: String, modifier: FileModifier): ObjectPreSignUrl
    fun touchFile(filePath: String, modifier: FileModifier, fileSizeInBytes: Long? = null, expiration: Duration? = null): TouchedFile

    fun setToTemp(filePath: String, throwIfNotStored: Boolean): FileIndex?
    fun saveFileIfTemp(filePath: String, fileType:String,  entityId: Long? = null, fileSizeInBytes: Long? = null, checkFileExisted: Boolean = true): FileIndex?
    fun saveFile(filePath: String, fileType:String,  entityId: Long? = null, fileSizeInBytes: Long? = null, checkFileExisted: Boolean = true): FileIndex?
    fun checkFileInStorage(filePath:String, throwIfNotStored: Boolean) : Boolean
    fun deleteFile(filePath: String, deleteObject:Boolean = false):Boolean
    fun deleteFiles(filePaths: List<String>, deleteObject: Boolean = false): Int
    fun copyFile(sourceFilePath: String, destFilePath: String, destModifier: FileModifier? = null, destFileType: String? = null, destEntityId: Long? = null): FileIndex

    fun clearTempFiles(durationAfterExpired: Duration = Duration.ofMinutes(10), batchSize:Int = 50, throwIfError: Boolean = false): Int

    fun makeTemp(stream: InputStream, filePath: String, modifier: FileModifier, length: Long? = null): FileIndex
}


fun FileIndex.isTempFile(): Boolean = this.fileType.equals(IFileIndexService.TEMP_FILE_TYPE, ignoreCase = true)

fun TempFileIndex.isExpired(): Boolean = this.timeExpired < System.currentTimeMillis()
fun TempFileIndex.isSafelyForDelete(durationAfterExpired: Duration = Duration.ofDays(1)): Boolean = (this.timeExpired + durationAfterExpired.toMillis()) < System.currentTimeMillis()


fun TempFileIndex.setTimeout(duration: Duration) {
    val seconds = duration.toMillis().coerceAtLeast(120 * 1000).coerceAtMost(Duration.ofDays(7).toMillis())
    this.timeExpired = System.currentTimeMillis() + seconds * 1000
    this.expirationSeconds = seconds
}