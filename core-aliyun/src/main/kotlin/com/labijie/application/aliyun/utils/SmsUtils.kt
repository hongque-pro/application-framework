package com.labijie.application.aliyun.utils

import com.aliyun.oss.ClientException
import com.aliyuncs.CommonRequest
import com.aliyuncs.IAcsClient
import com.aliyuncs.http.MethodType
import com.labijie.application.aliyun.AliyunProcessException
import com.labijie.application.aliyun.configuration.AliyunProperties
import com.labijie.application.aliyun.model.SmsResponse
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.logger
import java.rmi.ServerException


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-18
 */
open class SmsUtils internal constructor(private val client: IAcsClient, private val configuration: AliyunProperties) {

    fun sendSms(phoneNumber: String, templateCode: String, tempateParam: Any? = null): Boolean {
        val request = CommonRequest().apply {
            this.sysMethod = MethodType.POST
            this.sysDomain = "dysmsapi.aliyuncs.com"
            this.sysVersion = "2017-05-25"
            this.sysAction = "SendSms"
            if (!configuration.sms.region.isNullOrBlank()) {
                this.putQueryParameter("RegionId", configuration.sms.region)
            }
            this.putQueryParameter("PhoneNumbers", phoneNumber)
            this.putQueryParameter("SignName", configuration.sms.signName)
            this.putQueryParameter("TemplateCode", templateCode)
            if (tempateParam != null) {
                this.putQueryParameter("TemplateParam", JacksonHelper.serializeAsString(tempateParam, true))
            }
        }

        try {
            val response = client.getCommonResponse(request)
            val result = JacksonHelper.deserializeFromString(response.data, SmsResponse::class)
            val success = result.code.compareTo("OK", true)
            if(success != 0){
                logger.error("""Sending message was not successful, code: ${result.code}, info: ${result.message}.
                    | help docs : https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.2.14.5b1656e0jTfvs3
                """)
            }
            return success == 0
        } catch (e: ServerException) {
            throw AliyunProcessException("Send sms fault.", e)
        } catch (e: ClientException) {
            throw AliyunProcessException("Send sms fault.", e)
        }
    }
}