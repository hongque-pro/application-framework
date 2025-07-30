/**
 * @author Anders Xiao
 * @date 2025-07-11
 */
package com.labijie.application.component.storage

import com.labijie.application.BucketPolicy
import com.labijie.application.RequestUtils
import com.labijie.application.component.GenerationURLPurpose
import com.labijie.application.component.IObjectStorage
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.model.ObjectPreSignUrl
import com.labijie.infra.getApplicationName
import com.labijie.infra.utils.ifNullOrBlank
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.io.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString


class FileSystemObjectStorage : IObjectStorage {

    private val folder: String
    private var host: String? = null
    private var port: Int = 8080


    private var context: String = ""

    constructor(
        properties: FileSystemStorageProperties,
        environment: Environment,
    ) {
        val path = properties.baseFolder.ifNullOrBlank {
            val appName = environment.getApplicationName().ifNullOrBlank { ".objectStorage" }
            FilenameUtils.concat(System.getProperty("user.home"), appName).toString()
        }.let {
            val path = Path(it.replace("/", File.separator).removeSuffix("/"))
            if (path.isAbsolute) {
                path.absolutePathString()
            } else {
                Path(System.getProperty("user.home"), it).absolutePathString()
            }
        }

        val baseFolder = File(path)
        if (baseFolder.exists() && !baseFolder.isDirectory()) {
            throw IllegalArgumentException("FileSystemObjectStorage base folder already existed, but it is a file.\n${baseFolder.absolutePath}")
        }
        if (!baseFolder.exists()) {
            baseFolder.mkdirs()
            logger.info("FileSystemObjectStorage base folder created at: \n${baseFolder.absolutePath}")
        }
        this.folder = baseFolder.absolutePath.removeSuffix("/").removeSuffix(File.separator)
        context = environment.getProperty("server.context-path").orEmpty().trimStart('/').trimEnd('/')
        port = environment.getProperty("server.port").orEmpty().toIntOrNull() ?: 8080
        logger.info("File storage is running.\n Host: $host\n Folder:\n $folder")
    }

    constructor(baseFolder: String, hostUrl: String = "http://localhost:8080/") {
        folder = baseFolder
        host = hostUrl
        logger.info("File storage is running.\n Host: $host\n Folder:\n $folder")
    }


    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(FileSystemObjectStorage::class.java)
        }
    }

    private fun getBaseUrl(): String {
        val h = host
        if(!h.isNullOrBlank())
        {
            return h
        }

        val baseUrl = RequestUtils.getServerBaseUrl()
        if(!baseUrl.isNullOrBlank()) {
            val url = if (context.isNotBlank()) {
                "${baseUrl}/${context}"
            } else {
                baseUrl
            }
            host = url
            return url
        }

        val base = if(port == 80)  "http://localhost" else "http://localhost:${port}"

        return if (context.isNotBlank()) {
            "${base}/${context}"
        } else {
            base
        }
    }


    private fun getFullFileName(key: String, sourceBucket: BucketPolicy): File {
        val bucket = if (sourceBucket == BucketPolicy.PRIVATE) "private" else "public"
        val path = key.removePrefix("/").replace("/", File.separator)
        return File("${folder}${File.separator}${bucket}${File.separator}${path}")
    }

    private fun getFullUrl(key: String, sourceBucket: BucketPolicy): String {
        val bucket = if (sourceBucket == BucketPolicy.PRIVATE) "private" else "public"
        val path = key.removePrefix("/").replace(File.separator, "/")
        return "$host/${folder}/${bucket}/${path}"
    }

    override fun copyObject(sourceKey: String, sourceBucket: BucketPolicy, destKey: String, destBucket: BucketPolicy?) {
        val sourceFile = getFullFileName(sourceKey, sourceBucket)

        if (!sourceFile.exists()) {
            throw StoredObjectNotFoundException("$sourceFile was not found.")
        }

        val destFile = getFullFileName(destKey, destBucket ?: sourceBucket)
        if (sourceFile.path != destFile.path) {
            sourceFile.copyTo(destFile, overwrite = true)
        }
    }

    override fun deleteObject(key: String, bucketPolicy: BucketPolicy): Boolean {
        val file = getFullFileName(key, bucketPolicy)
        if (file.exists()) {
            file.delete()
            return true
        }
        return false
    }

    override fun existObject(key: String, throwIfNotExisted: Boolean, bucketPolicy: BucketPolicy): Boolean {
        val file = getFullFileName(key, bucketPolicy)
        if (throwIfNotExisted && (!file.exists() || !file.isFile)) {
            throw StoredObjectNotFoundException()
        }
        return file.exists()
    }

    override fun generateObjectUrl(
        key: String,
        bucketPolicy: BucketPolicy,
        purpose: GenerationURLPurpose,
        responseHeaderOverrides: HttpHeaders?,
        mimeToWrite: String?
    ): ObjectPreSignUrl {
        val file = getFullFileName(key, bucketPolicy)
        if (purpose == GenerationURLPurpose.Read && (!file.exists() || !file.isFile)) {
            throw StoredObjectNotFoundException()
        }

        if (purpose == GenerationURLPurpose.Write) {
            val bucket = if (bucketPolicy == BucketPolicy.PRIVATE) "private" else "public"
            val url = "$host/file-storage/upload/${bucket}/${key.removePrefix("/")}"
            return ObjectPreSignUrl(url)
        } else {
            val bucket = if (bucketPolicy == BucketPolicy.PRIVATE) "private" else "public"
            val url = "$host/file-storage/read/${bucket}/${key.removePrefix("/")}"
            return ObjectPreSignUrl(url)
        }
    }

    override fun getObject(key: String, bucketPolicy: BucketPolicy): ByteArray {
        val file = getFullFileName(key, bucketPolicy)
        if (!file.exists() || !file.isFile) {
            throw StoredObjectNotFoundException()
        }
        return file.readBytes()
    }

    override fun getObject(key: String, outputStream: OutputStream, bucketPolicy: BucketPolicy) {
        val file = getFullFileName(key, bucketPolicy)
        if (!file.exists() || !file.isFile) {
            throw StoredObjectNotFoundException()
        }
        FileInputStream(file).use {
            IOUtils.copy(it, outputStream)
        }
    }

    override fun getObjectSizeInBytes(key: String, bucketPolicy: BucketPolicy): Long? {
        val file = getFullFileName(key, bucketPolicy)
        if (!file.exists() || !file.isFile) {
            throw StoredObjectNotFoundException()
        }
        return file.length()
    }

    override fun uploadObject(key: String, stream: InputStream, bucketPolicy: BucketPolicy, contentLength: Long?) {
        val file = getFullFileName(key, bucketPolicy)
        val folder = file.parentFile
        if (!folder.exists()) {
            folder.mkdirs()
        }
        file.setWritable(true)

        FileOutputStream(file).use {
            IOUtils.copy(stream, it)
        }

    }
}