package com.labijie.application.minio.testing

import com.labijie.application.BucketPolicy
import com.labijie.application.minio.testing.TestKit.createMinioClient
import com.labijie.appliction.minio.MinioObjectStorage
import io.minio.MinioClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.util.*

class AssumeRoleTester : AbstractTester() {
    private val utils = TestKit.createMinioUtils()

    private fun assumeClient(): MinioObjectStorage {
        val credentials = utils.assumeRoleProvider()

        val client = createMinioClient(credentials)
        return MinioObjectStorage(TestKit.ApplicationName, TestKit.minioProperties, client)
    }

    @Test
    fun testAssumeRoleUpload() {
        val client = assumeClient()

        val content = ByteArrayInputStream("Test".toByteArray(Charsets.UTF_8))
        Assertions.assertDoesNotThrow {
            client.uploadObject(UUID.randomUUID().toString(), content, BucketPolicy.PRIVATE)
        }

        Assertions.assertDoesNotThrow {
            client.uploadObject(UUID.randomUUID().toString(), content, BucketPolicy.PUBLIC)
        }
    }

}