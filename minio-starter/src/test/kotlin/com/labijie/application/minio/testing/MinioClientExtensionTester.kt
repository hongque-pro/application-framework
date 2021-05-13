package com.labijie.application.minio.testing

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.test.Test

class MinioClientExtensionTester {

    @Test
    fun testMakeBucket() {
        val client = TestKit.createMinioClient()
        try {
            client.makeBucketIfNotExisted("test-private", BucketPolicy.PRIVATE)
            client.makeBucketIfNotExisted("test-public", BucketPolicy.PUBLIC)
        } catch (e: ConnectException) {
            e.printStackTrace()
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
        } finally {
            client.removeBucketIfExisted("test-private")
            client.removeBucketIfExisted("test-public")
        }
    }



}