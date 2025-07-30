package com.labijie.application.hcaptcha

import com.labijie.application.ApplicationErrors
import com.labijie.application.component.IHumanChecker
import com.labijie.application.hcaptcha.configuration.HCaptchaProperties
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
    restClient: RestClient.Builder
) : IHumanChecker {

    init {
        if (properties.secret.isNotBlank()) {
            throw IllegalStateException("Value of secret can not be blank in HCaptchaProperties")
        }
    }

    private val uri = properties.endpoint.ifNullOrBlank { HCaptchaProperties.DEFAULT_ENDPOINT }

    private val client = restClient.build()

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
            .retrieve().toEntity<HCapchaVerifyResponse>()

        val body = response.body
        if(!response.statusCode.is2xxSuccessful || body == null) {
            throw HCaptchaException(ApplicationErrors.UnhandledError)
        }

        body.errorCodes?.let {
            if(it.isNotEmpty()) {
                throw HCaptchaException(ApplicationErrors.UnhandledError)
            }
        }


        return body.success
    }

    override fun clientStampRequired(): Boolean {
        return false
    }

}