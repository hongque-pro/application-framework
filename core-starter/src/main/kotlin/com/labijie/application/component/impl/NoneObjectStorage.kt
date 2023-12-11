package com.labijie.application.component.impl

import com.labijie.application.BucketPolicy
import com.labijie.application.component.GenerationURLPurpose
import com.labijie.application.component.IObjectStorage
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.model.ObjectPreSignUrl
import java.io.InputStream
import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
open class NoneObjectStorage : IObjectStorage {
    override fun uploadObject(key: String, stream: InputStream, bucketPolicy: BucketPolicy, contentLength: Long?) {

    }

    override fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy, purpose: GenerationURLPurpose): ObjectPreSignUrl {
        return ObjectPreSignUrl(url = "", 0)
    }

    override fun deleteObject(key: String, bucketPolicy: BucketPolicy): Boolean {
        return false;
    }

    override fun existObject(key: String, throwIfNotExisted: Boolean, bucketPolicy: BucketPolicy): Boolean {
        if(throwIfNotExisted) throw StoredObjectNotFoundException()
        return false;
    }

    override fun getObject(key: String, bucketPolicy: BucketPolicy): ByteArray {
        return ByteArray(0)
    }
}