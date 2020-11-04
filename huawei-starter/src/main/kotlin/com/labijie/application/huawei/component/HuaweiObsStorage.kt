package com.labijie.application.huawei.component

import com.labijie.application.BucketPolicy
import com.labijie.application.component.IObjectStorage
import com.labijie.application.huawei.utils.HuaweiUtils
import java.io.InputStream
import java.net.URL

class HuaweiObsStorage(private val huaweiUtils: HuaweiUtils): IObjectStorage {
    override fun existObject(key: String, throwIfNotExisted: Boolean, bucketPolicy: BucketPolicy): Boolean {
        return huaweiUtils.existObject(key, throwIfNotExisted, bucketPolicy)
    }

    override fun deleteObject(key: String, bucketPolicy: BucketPolicy): Boolean {
        return huaweiUtils.deleteObject(key, bucketPolicy)
    }

    override fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy): URL {
        return huaweiUtils.generateObjectUrl(key, bucketPolicy)
    }

    override fun uploadObject(key: String, stream: InputStream, bucketPolicy: BucketPolicy) {
        return huaweiUtils.uploadObject(key, stream, bucketPolicy)
    }

    override fun getObject(key: String, bucketPolicy: BucketPolicy): ByteArray {
        return huaweiUtils.getObject(key, bucketPolicy)
    }
}