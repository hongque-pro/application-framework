package com.labijie.application.component

import com.labijie.application.BucketPolicy
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.model.ObjectPreSignUrl
import java.io.InputStream
import java.io.OutputStream
import javax.print.attribute.standard.Destination

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
    fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy = BucketPolicy.PRIVATE, purpose: GenerationURLPurpose = GenerationURLPurpose.Read): ObjectPreSignUrl
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