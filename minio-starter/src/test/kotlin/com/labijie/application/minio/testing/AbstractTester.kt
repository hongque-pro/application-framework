package com.labijie.application.minio.testing

import com.labijie.application.BucketPolicy
import com.labijie.appliction.minio.MinioObjectStorage
import com.labijie.appliction.minio.makeBucketIfNotExisted
import com.labijie.appliction.minio.removeBucketIfExisted
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open abstract class AbstractTester {

    protected val client = TestKit.createMinioClient()

    protected val storage by lazy {
        MinioObjectStorage(
            TestKit.ApplicationName,
            TestKit.minioProperties,
            client
        )
    }

    @AfterTest
    private fun cleanBuckets() {
        val bucket1 = storage.getBucket(BucketPolicy.PUBLIC)
        client.removeBucketIfExisted(bucket1, true)

        val bucket2 = storage.getBucket(BucketPolicy.PRIVATE)
        client.removeBucketIfExisted(bucket2, true)
    }

    @BeforeTest
    private fun initBuckets() {
        val bucket1 = storage.getBucket(BucketPolicy.PUBLIC)
        client.makeBucketIfNotExisted(bucket1, BucketPolicy.PUBLIC)

        val bucket2 = storage.getBucket(BucketPolicy.PRIVATE)
        client.makeBucketIfNotExisted(bucket2, BucketPolicy.PRIVATE)
    }
}