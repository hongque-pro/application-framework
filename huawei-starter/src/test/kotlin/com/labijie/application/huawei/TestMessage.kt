package com.labijie.application.huawei

import com.labijie.application.BucketPolicy
import com.labijie.application.huawei.model.HuaweiProperties
import com.labijie.application.huawei.model.HuaweiSmsTemplateParam
import com.labijie.application.huawei.utils.HuaweiUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit4.SpringRunner
import java.io.FileInputStream

@SpringBootTest(classes = [TestMessage::class])
@RunWith(SpringRunner::class)
@ComponentScan("com.labijie.application.huawei")
class TestMessage {

    @Autowired
    private lateinit var huaweiUtils: HuaweiUtils

    @Autowired
    private lateinit var huaweiProperties: HuaweiProperties

//    @Test
    fun testSendMessage() {
        huaweiProperties.smsSettings.appKey = "94ahfv5ogWrmkg92bt6D1m2tk8xL"
        huaweiProperties.smsSettings.appSecret = "5P6s9szIHtXgMd2LtKqxdn68Jb76"
        huaweiProperties.smsSettings.apiServer = "https://rtcsms.cn-north-1.myhuaweicloud.com:10743"
        huaweiUtils.sendSmsMessage("15812000297", "a01d71900aba4331a0df35699afa13e9", HuaweiSmsTemplateParam(arrayOf("908765"), "8820091606432", "大棒客"))
    }


    @Test
    fun testStorage() {
        huaweiProperties.obsSettings.endPoint = "obs.cn-north-4.myhuaweicloud.com"
        huaweiProperties.obsSettings.bucketName = "bigbang-nas"
        huaweiProperties.obsSettings.ak = "VCJCMPESERAWAMHBAIFO"
        huaweiProperties.obsSettings.sk = "DUkc2HQBw6bvuH6f9M3eR9D56Lt11NxatEsQqTRw"
        huaweiProperties.obsSettings.domain = "bigbang-nas.obs.cn-north-4.myhuaweicloud.com"

        huaweiUtils.uploadObject("test/1/1/1/t.c", FileInputStream("/home/evan/tmp/t.c"), BucketPolicy.PRIVATE)
        huaweiUtils.uploadObject("test/1/1/1/k.txt", FileInputStream("/home/evan/tmp/k.txt"), BucketPolicy.PUBLIC)
        huaweiUtils.existObject("test/1/1/1/t.c", false, BucketPolicy.PRIVATE).apply {
            println("exists:" + this)
        }
        huaweiUtils.getObject("test/1/1/1/t.c", BucketPolicy.PRIVATE).apply {
            System.err.write(this)
            System.err.flush()
        }
        huaweiUtils.deleteObject("test/1/1/1/t.c", BucketPolicy.PRIVATE)
        huaweiUtils.existObject("test/1/1/1/t.c", false, BucketPolicy.PRIVATE).apply {
            println("exists:" + this)
        }
        huaweiUtils.getObject("test/1/1/1/k.txt", BucketPolicy.PUBLIC).apply {
            System.err.write(this)
            System.err.flush()
        }

        huaweiUtils.uploadObject("test/1/1/1/t.c", FileInputStream("/home/evan/tmp/t.c"), BucketPolicy.PRIVATE)
        huaweiUtils.generateObjectUrl("test/1/1/1/t.c", BucketPolicy.PRIVATE).apply {
            println(this)
        }

        huaweiUtils.generateObjectUrl("test/1/1/1/k.txt", BucketPolicy.PUBLIC).apply {
            println(this)
        }
    }
}