package com.labijie.application.thridparty.alipay

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.labijie.application.crypto.RsaUtils
import com.labijie.application.exception.BadSignatureException
import com.labijie.application.exception.ThirdPartyExchangeException
import com.labijie.application.web.client.exchangeForString
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import com.labijie.infra.utils.throwIfNecessary
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import kotlin.reflect.KClass

object AlipayUtilities {

    const val SIGN_TYPE_RSA2 = "RSA2"
    const val SIGN_TYPE_RSA = "RSA"

    const val PLATFORM_NAME ="alipay"

    fun signature(params: Map<String, String>, privateKey:String, signType: String = SIGN_TYPE_RSA2): String {
        val type = signType.ifNullOrBlank { SIGN_TYPE_RSA2 }
        return when(type.lowercase()){
            SIGN_TYPE_RSA-> RsaUtils.rsaSignSHA1(params, privateKey)
            SIGN_TYPE_RSA2-> RsaUtils.rsaSignSHA256(params, privateKey)
            else-> {
                logger.warn("Alipay signature algorithm \"$signType\" was not supported, RSA2 has been used.")
                RsaUtils.rsaSignSHA256(params, privateKey)
            }
        }
    }



    fun verifySignature(sign: String, publicKey:String, params: Map<String, String>, signType: String): Boolean {
        val type = signType.ifNullOrBlank { SIGN_TYPE_RSA2 }
        return when(type.lowercase()){
            SIGN_TYPE_RSA2-> RsaUtils.verifySHA256(params, sign, publicKey)
            SIGN_TYPE_RSA -> RsaUtils.verifySHA1(params, sign, publicKey)
            else-> {
                logger.warn("Alipay signature algorithm \"$signType\" was not supported.")
                false
            }
        }
    }

    fun <T: AlipayResponseBase> requestAlipayApi(
        restTemplate: RestTemplate,
        url:String,
        params: MutableMap<String, String>,
        responseType: KClass<T>,
        publickKeyForVerify:String? = null): T {

        val map = LinkedMultiValueMap<String, String>(params.size).apply {
            params.forEach{
                this[it.key] = it.value
            }
        }

        val headers = HttpHeaders().apply {
            this.accept = listOf(MediaType.APPLICATION_JSON)
            this.contentType = MediaType.APPLICATION_FORM_URLENCODED
        }
        val entity = HttpEntity<Any?>(map, headers)

        try {
            //支付宝强制返回 text/html;charset=UTF-8 但是 body 是 json
            val response = restTemplate.exchangeForString(url, HttpMethod.POST, entity)
            val bodyString = response.body
            var respData:T? = null
            var jsonNode: JsonNode? = null
            if (response.statusCode == HttpStatus.OK && !bodyString.isNullOrBlank()) {
                jsonNode = JacksonHelper.webCompatibilityMapper.readTree(bodyString)
                val filedName = "${params["method"]!!.replace(".", "_")}_response"
                val sign = jsonNode.findValuesAsText("sign").firstOrNull()
                val data = jsonNode[filedName]
                //验签
                if(!publickKeyForVerify.isNullOrBlank()) {
                    val content = data.toString()
                    if(sign.isNullOrBlank() || !verifyResponseSign(sign, publickKeyForVerify, content)){
                        throw BadSignatureException(platform = PLATFORM_NAME)
                    }
                }

                respData = JacksonHelper.webCompatibilityMapper.convertValue(data, responseType.java)
                if (respData.code == "10000") {
                    return respData
                }
            }

            val prettyBody  = if(!bodyString.isNullOrBlank()) JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode ?: bodyString) else "<null>"
            val error = """Invoke $PLATFORM_NAME payment api fault.
                Request URL: $url
                HTTP STATUS: ${response.statusCodeValue}, 
                Alipay Response: $prettyBody
                Doc URL: https://docs.open.alipay.com/api_1/alipay.trade.create/ """

            throw ThirdPartyExchangeException(
                PLATFORM_NAME,
                error,
                platformErrorCode = (respData?.subCode).ifNullOrBlank(respData?.code)
            )
        }
        catch (pex: ThirdPartyExchangeException){
            throw pex
        }
        catch (jsonEx: JsonProcessingException){
            val error = "Deserialize $PLATFORM_NAME response data fault.";
            logger.error(error)
            throw ThirdPartyExchangeException(
                PLATFORM_NAME,
                error,
                jsonEx
            )
        }
        catch (e:Throwable){
            e.throwIfNecessary()
            throw ThirdPartyExchangeException(
                PLATFORM_NAME,
                cause = e
            )
        }
    }

    private fun verifyResponseSign(
        sign: String,
        publicKey:String,
        responseData: String,
        signType: String = SIGN_TYPE_RSA2
    ): Boolean {
        //参考：https://docs.open.alipay.com/200/106120
        val type = signType.ifNullOrBlank { SIGN_TYPE_RSA2 }
        //同步签名串没有经过 BASE 64 编码
        return when(type.lowercase()){
            SIGN_TYPE_RSA2-> RsaUtils.verifySHA256(responseData, sign, publicKey) || RsaUtils.verifySHA256(responseData.replace("/", "\\/"), sign, publicKey)
            SIGN_TYPE_RSA -> RsaUtils.verifySHA1(responseData, sign, publicKey) || RsaUtils.verifySHA1(responseData.replace("/", "\\/"), sign, publicKey)
            else-> {
                logger.warn("Alipay signature algorithm \"$signType\" was not supported.")
                false
            }
        }
    }


}