package com.labijie.application.thridparty.wechat

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.labijie.application.SpringContext
import com.labijie.application.crypto.HashUtils
import com.labijie.application.exception.BadSignatureException
import com.labijie.application.exception.ThirdPartyExchangeException
import com.labijie.application.web.client.exchangeForString
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import com.labijie.infra.utils.throwIfNecessary
import com.sun.xml.internal.stream.writers.UTF8OutputStreamWriter
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import java.time.format.DateTimeFormatter
import javax.swing.Spring
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter
import kotlin.reflect.KClass

object WechatUtilities {

    const val DATETIME_FORMAT_PATTERN = "yyyyMMddHHmmss"
    val DATETIME_FORMAT = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)

    const val PLATFORM_NAME = "wechat"

    val xmlMapper by lazy {
        XmlMapper.builder()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            //序列化时指定根元素，true，则以类名为根元素
            .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
            //忽略空属性
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
            //ML标签名:使用骆驼命名的属性名
            //设置转换模式
            .enable(MapperFeature.USE_ANNOTATIONS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build()

    }

    private fun XMLStreamWriter.closeIgnoreException() {
        try {
            this.close()
        } catch (_: XMLStreamException) {

        }
    }

    const val SIGN_TYPE_HMAC_SHA256 = "HMAC-SHA256";
    const val SIGN_TYPE_MD5 = "MD5"

    fun signature(params: Map<String, String>, key: String, signType: String? = null): String {
        val type = signType.ifNullOrBlank { SIGN_TYPE_HMAC_SHA256 }
        return when (type.lowercase()) {
            SIGN_TYPE_MD5 -> HashUtils.signMD5(params, key)
            SIGN_TYPE_HMAC_SHA256 -> HashUtils.signHmacSha256(params, key)
            else -> {
                logger.warn("Wechat signature algorithm \"$signType\" was not supported, HMAC-SHA256 has been used.")
                HashUtils.signHmacSha256(params, key)
            }
        }
    }

    fun verifySignature(sign: String, key: String, params: Map<String, String>, signType: String? = null): Boolean {
        val type = signType.ifNullOrBlank { SIGN_TYPE_HMAC_SHA256 }
        return when (type.lowercase()) {
            SIGN_TYPE_MD5 -> HashUtils.verifyMD5(params, sign, key)
            SIGN_TYPE_HMAC_SHA256 -> HashUtils.verifyHmacSha256(params, sign, key)
            else -> {
                logger.warn("Wechat signature algorithm \"$signType\" was not supported.")
                false
            }
        }
    }

    /**
     * 创建微信特有格式的 XML
     */
    fun buildXmlBody(parameter: Map<String, String>, useCDataContent: Boolean = true): String {
        val xmlOutputFactory = XMLOutputFactory.newFactory()
        return try {
            StringWriter().use { out ->
                val sw = xmlOutputFactory.createXMLStreamWriter(out)
                try {
                    sw.writeStartDocument()
                    sw.writeStartElement("xml")
                    parameter.forEach { (key, value) ->
                        sw.writeStartElement(key)
                        if (useCDataContent) sw.writeCData(value) else sw.writeCharacters(value)
                        sw.writeEndElement()
                    }
                    sw.writeEndElement()
                    sw.writeEndDocument()
                    sw.flush()
                } finally {
                    sw.closeIgnoreException()
                }
                out.toString()
            }
        } catch (xe: XMLStreamException) {
            throw ThirdPartyExchangeException(
                PLATFORM_NAME,
                "Build request xml parameter fault.",
                xe
            )
        }
    }


    fun <T : WechatResponse> requestWechatApi(
        restTemplate: RestTemplate,
        url: String,
        params: MutableMap<String, String>,
        responseType: KClass<T>,
        signKeyForVerify: String? = null
    ): T {
        val headers = HttpHeaders().apply {
            this.accept = listOf(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.TEXT_PLAIN)
            this.contentType = MediaType.APPLICATION_XML
        }
        val body = buildXmlBody(params)
        val entity = HttpEntity(body, headers)

        try {
            val response = restTemplate.exchangeForString(url, HttpMethod.POST, entity)

            var respData: T? = null
            if (response.statusCode == HttpStatus.OK) {
                val tree = xmlMapper.readTree(response.body) //读取 tree 方便转换到 Map 来验签
                respData = xmlMapper.convertValue(tree, responseType.java)

                //验签
                if (!signKeyForVerify.isNullOrBlank()) {
                    val sign = respData.sign
                    val data = JacksonHelper.defaultObjectMapper.convertValue<MutableMap<String, String>>(
                        tree,
                        object : TypeReference<MutableMap<String, String>>() {})
                    data.remove("sign")
                    if (!sign.isNullOrBlank() && !verifySignature(
                            sign,
                            signKeyForVerify,
                            data
                        )
                    ) {
                        throw BadSignatureException(platform = PLATFORM_NAME)
                    }
                }

                if (respData.returnCode == WechatResponse.RETURN_CODE_SUCCESS && respData.resultCode == WechatResponse.RESULT_CODE_SUCCESS) {
                    return respData
                }
            }

            val error = """
                Invoke $PLATFORM_NAME api fault.
                Request URL: $url
                HTTP STATUS: ${response.statusCodeValue}, 
                Wechat Response: ${response.body.ifNullOrBlank { "<null>" }}
                
                Doc URL: https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1""".trimIndent()

            logger.error(error)


            throw ThirdPartyExchangeException(
                PLATFORM_NAME,
                if (SpringContext.isDevelopment || SpringContext.isTest) error else "Invoke ${PLATFORM_NAME} api fault",
                platformErrorCode = respData?.errorCode
            )
        } catch (e: Throwable) {
            when (e) {
                is XMLStreamException,
                is JsonProcessingException -> {
                    val error = "Deserialize $PLATFORM_NAME response data fault.";
                    logger.error(error)
                    throw ThirdPartyExchangeException(
                        PLATFORM_NAME,
                        error,
                        e
                    )
                }
                is ThirdPartyExchangeException -> throw e
                else -> {
                    e.throwIfNecessary()
                    throw ThirdPartyExchangeException(
                        PLATFORM_NAME,
                        cause = e
                    )
                }
            }
        }
    }


}