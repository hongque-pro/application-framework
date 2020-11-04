package com.labijie.application.payment.testing

import com.fasterxml.jackson.core.type.TypeReference
import com.labijie.application.payment.PaymentUtils
import com.labijie.application.thridparty.wechat.WechatUtilities
import com.labijie.infra.json.JacksonHelper
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class XmlBodyTester {

    @Test
    fun testDeserialize() {
        val body = """
<xml>
  <appid><![CDATA[wx2421b1c4370ec4]]></appid>
  <sub_appid>wx2421b1c4370ec13b</sub_appid>
  <attach><![CDATA[支付测试]]></attach>
  <bank_type><![CDATA[CFT]]></bank_type>
  <fee_type><![CDATA[CNY]]></fee_type>
  <is_subscribe><![CDATA[Y]]></is_subscribe>
  <mch_id><![CDATA[10000100]]></mch_id>
  <nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>
  <openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>
  <out_trade_no><![CDATA[1409811653]]></out_trade_no>
  <result_code><![CDATA[SUCCESS]]></result_code>
  <return_code><![CDATA[SUCCESS]]></return_code>
  <sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>
  <sub_mch_id><![CDATA[10000100]]></sub_mch_id>
  <time_end><![CDATA[20140903131540]]></time_end>
  <total_fee>1</total_fee>

<coupon_fee><![CDATA[10]]></coupon_fee>
<coupon_count><![CDATA[1]]></coupon_count>
<coupon_type><![CDATA[CASH]]></coupon_type>
<coupon_id><![CDATA[10000]]></coupon_id>
<coupon_fee><![CDATA[100]]></coupon_fee>
  <trade_type><![CDATA[JSAPI]]></trade_type>
  <transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>
</xml>
        """.trimIndent()

        val json = WechatUtilities.xmlMapper.readValue<Map<String, String>>(body, object: TypeReference<Map<String, String>>() {})
        println(JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json))

        Assertions.assertTrue(json.isNotEmpty())
        Assertions.assertEquals("wx2421b1c4370ec4", json["appid"])
        Assertions.assertEquals("1004400740201409030005092168", json["transaction_id"])
    }
}