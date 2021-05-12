package com.labijie.application.minio.testing

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.makeBucketIfNotExisted
import io.minio.MinioClient
import io.minio.RemoveBucketArgs
import java.net.ConnectException
import java.net.SocketTimeoutException
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
        catch (e:SocketTimeoutException){
            e.printStackTrace()
        }
        finally {
            deleteBucket(client, "test")
        }
    }

    private fun deleteBucket(client: MinioClient, bucket:String){
        try {
            client.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build())
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}