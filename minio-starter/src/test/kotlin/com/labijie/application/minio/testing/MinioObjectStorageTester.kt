package com.labijie.application.minio.testing

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.MinioObjectStorage
import com.labijie.appliction.minio.makeBucketIfNotExisted
import com.labijie.appliction.minio.removeBucketIfExisted
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class MinioObjectStorageTester {

    private val client = MinioTestHelper.createMinioClient()

    private val storage = MinioObjectStorage(
        "test",
        MinioTestHelper.minioProperties,
        client
    )

    @AfterTest
    private fun cleanBuckets(){
        val bucket1 = storage.getBucket(BucketPolicy.PUBLIC)
        client.removeBucketIfExisted(bucket1)

        val bucket2 = storage.getBucket(BucketPolicy.PRIVATE)
        client.removeBucketIfExisted(bucket2)
    }

    @BeforeTest
    private fun initBuckets(){
        val bucket1 = storage.getBucket(BucketPolicy.PUBLIC)
        client.makeBucketIfNotExisted(bucket1, BucketPolicy.PUBLIC)

        val bucket2 = storage.getBucket(BucketPolicy.PRIVATE)
        client.makeBucketIfNotExisted(bucket2, BucketPolicy.PRIVATE)
    }

    @Test
    fun testDeleteNotExisted(){
        val deleted =storage.deleteObject(UUID.randomUUID().toString(), BucketPolicy.PRIVATE)
        Assertions.assertFalse(deleted)
    }
}