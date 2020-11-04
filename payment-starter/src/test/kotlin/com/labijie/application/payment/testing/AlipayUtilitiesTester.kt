package com.labijie.application.payment.testing

import com.labijie.application.thridparty.alipay.AlipayUtilities
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

class AlipayUtilitiesTester {


    @Test
    fun testVerifyResponse(){

        //AlipayUtilities.verifyResponseSign
        val method  = AlipayUtilities::class.declaredMemberFunctions.first { it.name == "verifyResponseSign" }
        method.isAccessible = true
        val content = "{\"code\":\"40004\",\"msg\":\"Business Failed\",\"sub_code\":\"ACQ.TRADE_NOT_EXIST\",\"sub_msg\":\"交易不存在\",\"buyer_pay_amount\":\"0.00\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"123456789\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\"}"
        val sign = "Jez60V7g2J1NwTCUoEB1G4u45KeYJZFpcJZ/12QqY+FZn7XzlriyT6uCevnU0XtfxbOtyoPIzGl+VCwKT3oVuLSWhiwKaSwdQon/vhZfj/Y0UMoHZFXdD9sHTepoHVROKOzU8IHq+b/aeoom4C5QmF+IQRVabsGtuc/TtO4KJ5nCDlhai3ZKZAa1+zWWIZ0k1008N4bI0JHTDYdCgfO0vdtZYdcC3pBSOasX/i7GCbsxf0ZNCEyhW7yYNj0rA2WmTht5cF+xoVn24kPNG6rwcwJFoNwQpBy6K7FfNp0M9CyZRgOjcYAeL5xPdXbbDvAs8dzSQtuKb6YBcADXI1l/bw=="
        val pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmW0ch5qsRjAK3UoOPHl+dJZCp8bniSPPK0CaM40m+b/nizr++AAuDyVVY3bNdMbewBCRfgNQ4DTYabIS+wTIn2AQPx19BmQyGzNJw4rgKXsDDfUSAmyDlIgwoeGVSY45X58BnBVNmYAaa4ucGxwrMNFeSE6R4Ds9tbjA+s2gg1dW2+hgoAGyuT0GnWVtgw4wiBcDXYr4/gYs8AzZJTFItXce+E5p+C9KHgIhY9vU+fww+q7y3GMn3EqMkhLe9pDdqY5TejwG5GiRz4OtQK4zCnzrFo9gRn626Q9NNdX+OGRYrNUGhpcF8BVfBrm+KTCp/QIEOa/lr6FbxRBY6mFzIwIDAQAB"
        val signValid = method.call(AlipayUtilities, sign, pubKey, content, AlipayUtilities.SIGN_TYPE_RSA2) as Boolean

        Assertions.assertTrue(signValid)
    }
}