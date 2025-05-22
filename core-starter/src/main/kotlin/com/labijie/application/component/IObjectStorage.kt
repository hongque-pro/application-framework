package com.labijie.application.component

import com.labijie.application.BucketPolicy
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.model.ObjectPreSignUrl
import com.labijie.infra.utils.ifNullOrBlank
import org.apache.commons.io.FilenameUtils
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import java.io.InputStream
import java.io.OutputStream

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
interface IObjectStorage {
    @Throws(StoredObjectNotFoundException::class)
    fun existObject(
        key: String,
        throwIfNotExisted: Boolean = false,
        bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE
    ): Boolean

    fun deleteObject(key: String, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE): Boolean
    fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE, purpose: GenerationURLPurpose = GenerationURLPurpose.Read, responseHeaderOverrides: HttpHeaders? = null, mimeToWrite: String? = null): ObjectPreSignUrl

    fun generateObjectReadUrl(key: String, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE): ObjectPreSignUrl {
        if(key.isBlank()) {
            throw IllegalArgumentException("Object key can not be empty.")
        }
        return generateObjectUrl(key, bucketPolicy, purpose = GenerationURLPurpose.Read)
    }

    fun generateObjectDownloadUrl(key: String, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE, defaultSaveFileName: String?): ObjectPreSignUrl {
        if(key.isBlank()) {
            throw IllegalArgumentException("Object key can not be empty.")
        }
        val fileName = defaultSaveFileName.ifNullOrBlank { FilenameUtils.getName(key) }
        val headers = HttpHeaders().apply {
            this.contentDisposition = ContentDisposition.attachment().filename(fileName).build()
        }
        return generateObjectUrl(key, bucketPolicy, purpose = GenerationURLPurpose.Read, headers)
    }

    fun generateObjectUploadUrl(key: String, mime: String, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE): ObjectPreSignUrl {
        if(key.isBlank()) {
            throw IllegalArgumentException("Object key can not be empty.")
        }
        return generateObjectUrl(key, bucketPolicy, purpose = GenerationURLPurpose.Write, null, mime)
    }

    fun uploadObject(
        key: String,
        stream: InputStream,
        bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE,
        contentLength: Long? = null
    )

    fun getObject(key: String, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE): ByteArray

    fun getObject(key: String, outputStream: OutputStream, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE)

    fun copyObject(sourceKey: String, sourceBucket: BucketPolicy, destKey: String, destBucket: BucketPolicy? = null)

    fun getObjectSizeInBytes(key: String, bucketPolicy: BucketPolicy): Long?
}

fun IObjectStorage.getObjectSizeInBytesChecked(key: String, bucketPolicy: BucketPolicy): Long {
    return getObjectSizeInBytes(key, bucketPolicy) ?: throw StoredObjectNotFoundException()
}

enum class GenerationURLPurpose {
    Read,
    Write
}