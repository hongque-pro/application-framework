package com.labijie.application.aliyun.utils

import com.aliyun.oss.ClientBuilderConfiguration
import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.ObjectMetadata
import com.aliyun.oss.model.PutObjectResult
import com.labijie.application.aliyun.AliyunProcessException
import com.labijie.application.aliyun.OssPolicy
import com.labijie.application.aliyun.configuration.AliyunProperties
import com.labijie.application.aliyun.configuration.BucketProperties
import java.io.InputStream
import java.net.URL
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-21
 */
open class OSSUtils internal constructor(private val configuration: AliyunProperties) {
    val settings
        get() = configuration.oss

    private fun createClient(policy: OssPolicy): OSS {
        val oss = getBucketProperties(policy)
        val endpoint =
            (if (settings.useInternal) oss.internalEndPoint else oss.endpoint)
                ?: throw AliyunProcessException("Aliyun oss internal endpoint or endpoint must be configured (${if (policy == OssPolicy.PRIVATE) "privateBucket" else "publicBucket"})")
        val url = endpoint.toString()

        val config = ClientBuilderConfiguration().apply {
            this.connectionTimeout = 5000
            this.requestTimeout = 10000
            this.socketTimeout = 10000
        }
        return OSSClientBuilder().build(url, configuration.accessKeyId, configuration.accessKeySecret, config)
    }

    private fun getBucketProperties(policy: OssPolicy): BucketProperties {
        return if (policy == OssPolicy.PUBLIC) configuration.oss.public else configuration.oss.private
    }

    private fun <T> useClient(policy: OssPolicy, action: (client: OSS) -> T): T {
        val client = createClient(policy)
        return try {
            action.invoke(client)
        } finally {
            client.shutdown()
        }
    }

    open fun putObject(key: String, stream: InputStream, policy: OssPolicy = OssPolicy.PRIVATE, metadata: ObjectMetadata?): PutObjectResult {
        return useClient(policy) {
            val bucket = getBucketProperties(policy)
            if(metadata != null){
                it.putObject(bucket.bucket, key, stream, metadata)
            }else{
                it.putObject(bucket.bucket, key, stream)
            }
        }
    }

    open fun deleteObject(key: String, policy: OssPolicy = OssPolicy.PRIVATE) {
        return useClient(policy) {
            val bucket = getBucketProperties(policy)
            it.deleteObject(bucket.bucket, key)
        }
    }

    open fun existObject(key: String, policy: OssPolicy = OssPolicy.PRIVATE): Boolean {
        return useClient(policy) {
            val bucket = getBucketProperties(policy)
            it.doesObjectExist(bucket.bucket, key)
        }
    }

    open fun getObjectUrl(key: String, policy: OssPolicy = OssPolicy.PRIVATE): URL {

        return useClient(policy) {
            val bucket = getBucketProperties(policy)
            val expiration = Date(System.currentTimeMillis() + 3600 * 1000)
            if (policy == OssPolicy.PRIVATE) {
                it.generatePresignedUrl(bucket.bucket, key, expiration)
            } else {
                val domain = if (bucket.customDomain.isBlank()) "${bucket.bucket}.${bucket.region}.aliyuncs.com"  else bucket.customDomain
                val fileName = key.removePrefix("/")

                URL("https://$domain/$fileName")
            }
        }
    }

    open fun getObject(key: String, policy: OssPolicy = OssPolicy.PRIVATE): ByteArray {
        return useClient(policy) {
            val bucket = getBucketProperties(policy)
            val obj = it.getObject(bucket.bucket, key)
            obj.use {
                obj.objectContent.use {
                    obj.objectContent.readBytes()
                }
            }
        }
    }
}