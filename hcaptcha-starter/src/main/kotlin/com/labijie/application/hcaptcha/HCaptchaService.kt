package com.labijie.application.hcaptcha

import com.fasterxml.jackson.core.JsonParseException
import com.labijie.application.component.IHumanChecker
import com.labijie.application.hcaptcha.configuration.HCaptchaProperties
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
class HCaptchaService(
    private val properties: HCaptchaProperties,
    restClient: RestClient.Builder?
) : IHumanChecker {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(HCaptchaService::class.java)
        }
    }

    init {
        if (properties.secret.isBlank()) {
            throw IllegalStateException("Value of secret can not be blank in HCaptchaProperties")
        }
    }

    private val uri = properties.apiEndpoint.ifNullOrBlank { HCaptchaProperties.DEFAULT_ENDPOINT }

    private val client = restClient?.build() ?: RestClient.builder().build()

    //refer: https://docs.hcaptcha.com/#verify-the-user-response-server-side
    override fun check(token: String, clientStamp: String?, clientIp: String?): Boolean {

        val uri = UriComponentsBuilder.fromUri(URI.create(uri))
            .queryParam("secret", properties.secret)
            .apply {
                clientIp?.let { this.queryParam("remoteip", it) }
            }
            .queryParam("response", token)
            .build()
            .toUri()

        val response = client.post()
            .uri(uri)
            .headers { headers ->
                headers.accept = listOf(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
            }
            .retrieve().toEntity<String>()

        val body: String? = response.body
        if(!response.statusCode.is2xxSuccessful || body == null) {
            logger.error("HCaptchaVerifyResponse returned status code ${response.statusCode}")
            return false
        }

        val hr = try {
            JacksonHelper.deserializeFromString(body, HCapchaVerifyResponse::class)
        }catch (e: JsonParseException){
            logger.error("HCaptcha response deserialization failed.\n${body}", e)
            null
        }
        hr?.errorCodes?.let {
                errorCodes->
            if(errorCodes.isNotEmpty()) {
                if(properties.logError) {
                    logger.warn("HCaptcha errors:\n${errorCodes.joinToString("\n") { "- ${it}" }}")
                }
                return false
            }
        }
        return hr?.success ?: false
    }

    override fun clientStampRequired(): Boolean {
        return false
    }

}