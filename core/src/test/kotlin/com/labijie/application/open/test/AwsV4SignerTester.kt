package com.labijie.application.open.test

import com.labijie.application.thridparty.aws.AwsV4Signer
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class AwsV4SignerTester {

    private val host = "example.amazonaws.com"
    private val accessKey = "AKIDEXAMPLE"
    private val secretKey = "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY"

    private val region = "us-east-1"
    private val service = "iam"


    @Test
    fun testSample() {


        //https://docs.amazonaws.cn/general/latest/gr/sigv4-create-canonical-request.html
        //https://docs.aws.amazon.com/general/latest/gr/sigv4-create-string-to-sign.html
        val signString = AwsV4Signer.Builder(accessKey, secretKey)
            .regionName(region)
            .serviceName(service)
            .httpMethodName("GET")
            .uri("http://iam.amazonaws.com/?/?Action=ListUsers&Version=2010-05-08")
            .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
            .build()
            .useAmzDateString("20150830T123600Z") //forTest
            .sign()

        Assertions.assertEquals("5d672d79c15b13162d9279b0855cfba6789a8edb4c82c400e06b5924a6f2b5d7", signString)
    }

}