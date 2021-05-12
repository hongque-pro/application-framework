package com.labijie.application.minio.testing

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.makeBucketIfNotExisted
import java.net.ConnectException
import kotlin.test.Test

class MinioClientExtensionTester {

    @Test
    fun testMakeBucket(){
        val client = MinioTestHelper.createMinioClient()
        try {
            client.makeBucketIfNotExisted("test", BucketPolicy.PUBLIC)
            client.makeBucketIfNotExisted("test", BucketPolicy.PUBLIC)
        }catch (e: ConnectException){
            e.printStackTrace()
        }
    }

}