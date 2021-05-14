package com.labijie.application.minio.testing

import com.labijie.application.BucketPolicy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.util.*

class MinioObjectStorageTester : AbstractTester() {
    @Test
    fun testDeleteNothing() {
        storage.deleteObject(UUID.randomUUID().toString(), BucketPolicy.PRIVATE)
        storage.deleteObject(UUID.randomUUID().toString(), BucketPolicy.PUBLIC)
    }

    @Test
    fun testExist() {
        var existed = storage.existObject(UUID.randomUUID().toString(), false, BucketPolicy.PRIVATE)
        Assertions.assertFalse(existed)
    }

    @Test
    fun testUpload() {
        val key = UUID.randomUUID().toString()

        storage.uploadObject(
            key,
            ByteArrayInputStream("Test".toByteArray()),
            BucketPolicy.PRIVATE,
            "Test".toByteArray().size.toLong()
        )

        val existed = storage.existObject(key, false, BucketPolicy.PRIVATE)
        Assertions.assertTrue(existed)
    }

    @Test
    fun testUploadOverride() {
        val key = UUID.randomUUID().toString()

        val content1 = UUID.randomUUID().toString()
        storage.uploadObject(
            key,
            ByteArrayInputStream(content1.toByteArray(Charsets.UTF_8)),
            BucketPolicy.PRIVATE,
            content1.toByteArray().size.toLong()
        )

        val got1 = storage.getObject(key, BucketPolicy.PRIVATE).toString(Charsets.UTF_8)
        Assertions.assertEquals(content1, got1)

        val content2 = UUID.randomUUID().toString()

        storage.uploadObject(
            key,
            ByteArrayInputStream(content2.toByteArray(Charsets.UTF_8)),
            BucketPolicy.PRIVATE,
            content2.toByteArray().size.toLong()
        )

        val got2 = storage.getObject(key, BucketPolicy.PRIVATE).toString(Charsets.UTF_8)
        Assertions.assertEquals(content2, got2)
    }

    @Test
    fun testDeleteExisted() {
        val key = UUID.randomUUID().toString()
        storage.uploadObject(
            key,
            ByteArrayInputStream("Test".toByteArray()),
            BucketPolicy.PRIVATE,
            "Test".toByteArray().size.toLong()
        )

        var existed = storage.existObject(key, false, BucketPolicy.PRIVATE)
        Assertions.assertTrue(existed)

        storage.deleteObject(key, BucketPolicy.PRIVATE)

        existed = storage.existObject(key, false, BucketPolicy.PRIVATE)
        Assertions.assertFalse(existed)
    }

}