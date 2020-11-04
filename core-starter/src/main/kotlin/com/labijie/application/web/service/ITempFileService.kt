package com.labijie.application.web.service

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
interface ITempFileService {
    fun saveTempFile(filePath: String, fileType:String):Boolean
    fun checkFile(filePath:String)
    fun deleteFile(filePath: String, deleteObject:Boolean = false):Boolean
    fun deleteFiles(filePath: List<String>, deleteObject: Boolean = false): Int
}