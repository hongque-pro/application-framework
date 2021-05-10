package com.labijie.application.aliyun.impl.component

import com.aliyun.oss.OSSErrorCode
import com.aliyun.oss.OSSException
import com.aliyun.oss.model.ObjectMetadata
import com.labijie.application.BucketPolicy
import com.labijie.application.aliyun.AliyunUtils
import com.labijie.application.aliyun.OssPolicy
import com.labijie.application.aliyun.impl.AliyunModuleInitializer
import com.labijie.application.component.IObjectStorage
import com.labijie.application.exception.StoredObjectNotFoundException
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
@Component
class AliyunOssStorage(private val aliyunUtils: AliyunUtils) : IObjectStorage {

    companion object{
        @JvmStatic
        fun BucketPolicy.toOssPolicy(): OssPolicy {
            return if(this == BucketPolicy.PUBLIC) OssPolicy.PUBLIC else OssPolicy.PRIVATE
        }
    }

    init {
        AliyunModuleInitializer.ossStorageEnabled = true
    }

    override fun getObject(key: String, bucketPolicy: BucketPolicy): ByteArray {
        return aliyunUtils.oss.getObject(key, bucketPolicy.toOssPolicy())
    }

    override fun uploadObject(key: String, stream: InputStream, bucketPolicy: BucketPolicy, contentLength: Long?) {
        val metadata = contentLength?.let {
            ObjectMetadata().apply {
                this.contentLength = it
            }
        }
        aliyunUtils.oss.putObject(key, stream, bucketPolicy.toOssPolicy(), metadata)
    }

    override fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy): URL {
        return aliyunUtils.oss.getObjectUrl(key, bucketPolicy.toOssPolicy())
    }

    override fun existObject(key: String, throwIfNotExisted: Boolean, bucketPolicy: BucketPolicy): Boolean {
            val existed = aliyunUtils.oss.existObject(key.trimStart('/'), bucketPolicy.toOssPolicy())
            if (throwIfNotExisted && !existed) {
                throw StoredObjectNotFoundException()
            }
            return existed
    }

    override fun deleteObject(key: String, bucketPolicy: BucketPolicy): Boolean {
        try {
            aliyunUtils.oss.deleteObject(key, bucketPolicy.toOssPolicy())
        }catch (e:OSSException){
            if(e.errorCode == OSSErrorCode.NO_SUCH_KEY){
                return false
            }
            throw e
        }
        return true
    }
}