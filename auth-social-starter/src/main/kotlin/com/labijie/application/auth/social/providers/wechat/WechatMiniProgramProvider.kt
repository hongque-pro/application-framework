package com.labijie.application.auth.social.providers.wechat

import com.labijie.application.auth.social.abstraction.AbstractMiniProgramProvider
import com.labijie.application.auth.social.exception.SocialExchangeException
import com.labijie.application.auth.social.exception.SocialDataDecryptionException
import com.labijie.application.auth.social.providers.wechat.model.Code2SessionResponse
import com.labijie.application.auth.social.providers.wechat.model.MobileResponse
import com.labijie.application.exception.BadSignatureException
import com.labijie.application.formUrlEncode
import com.labijie.application.identity.model.PlatformAccessToken
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import com.labijie.infra.utils.throwIfNecessary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
class WechatMiniProgramProvider(
    private val restTemplate: RestTemplate,
    options: WechatMiniOptions
) : AbstractMiniProgramProvider<WechatMiniOptions>(options) {

    override fun exchangeToken(authorizationCode: String): PlatformAccessToken {
        //https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
        //appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        val url = this.options.exchageUrl
        val query = mapOf(
            "appid" to options.appId,
            "secret" to options.appSecret,
            "js_code" to authorizationCode,
            "grant_type" to "authorization_code"
        )
        val queryString = query.formUrlEncode(true)
        val fullUrl = if (queryString.isBlank()) url else "$url?$queryString"

        val headers = HttpHeaders().apply {
            this.accept = listOf(MediaType.APPLICATION_JSON)
        }
        val entity = HttpEntity(null, headers)
        val result = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String::class.java)
        val response = JacksonHelper.deserializeFromString(result.body ?: "{}", Code2SessionResponse::class, true)
        if (result.statusCodeValue == 200 && response.sessionKey.isNotBlank()) {
            return PlatformAccessToken(response.unionId.ifNullOrBlank { response.openId }, response.sessionKey, appId =  options.appId, appOpenId =  response.openId)
        }
        val error = """Request wechat code2Session interface fault.
                 http status: ${result.statusCodeValue}
                 error code: ${response.errorCode}
                 error message:${response.errorMessage} """
        logger.error(error)

        throw SocialExchangeException(this.options.providerName, error.trimIndent())
    }

    //参考：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html
    override fun decryptTurelyPhoneNumber(encryptedData: String, token: PlatformAccessToken, iv: String?): String {
        try {
            val ivStr = iv ?: throw IllegalArgumentException("Aes iv cant not be null")
            val data = WechatCrypto.decryptAes(encryptedData, token.sessionKey, ivStr)
            val r = JacksonHelper.deserializeFromString(data, MobileResponse::class)
            if (r.watermark.appId != this.options.appId) {
                throw BadSignatureException("Bad watermark for wechat data.", this.options.providerName)
            }
            return r.phoneNumber
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw SocialDataDecryptionException(this.options.providerName)
        }
    }
}