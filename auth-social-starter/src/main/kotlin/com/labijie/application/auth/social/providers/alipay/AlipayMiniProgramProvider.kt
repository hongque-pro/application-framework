package com.labijie.application.auth.social.providers.alipay

import com.labijie.application.auth.social.abstraction.AbstractMiniProgramProvider
import com.labijie.application.auth.social.exception.SocialDataDecryptionException
import com.labijie.application.auth.social.exception.SocialExchangeException
import com.labijie.application.auth.social.providers.alipay.model.MobileResponse
import com.labijie.application.auth.social.providers.alipay.model.OAuthTokenResponse
import com.labijie.application.crypto.AesException
import com.labijie.application.crypto.AesUtils
import com.labijie.application.crypto.RsaException
import com.labijie.application.crypto.RsaUtils
import com.labijie.application.exception.BadSignatureException
import com.labijie.application.identity.model.PlatformAccessToken
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.time.Duration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
class AlipayMiniProgramProvider(
    options: AlipayMiniOptions,
    private val restTemplate: RestTemplate
) : AbstractMiniProgramProvider<AlipayMiniOptions>(options) {

    //文档参考 https://opendocs.alipay.com/mini/introduce/authcode
    override fun exchangeToken(authorizationCode: String): PlatformAccessToken {
        //接口文档 https://docs.open.alipay.com/api_9/alipay.system.oauth.token
        val params = options.buildCommonsParam("alipay.system.oauth.token")
        params["grant_type"] = "authorization_code"
        params["code"] = authorizationCode

        options.signParameters(params)

        val uri = UriComponentsBuilder.fromHttpUrl(options.exchageUrl).apply {
            params.forEach { k, v ->
                this.queryParam(k, URLEncoder.encode(v, "UTF-8"))
            }}
            .build(true)
            .toUri()

        val headers = HttpHeaders().apply {
            this.accept = listOf(MediaType.APPLICATION_JSON)
        }
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, String::class.java)
        var respData: OAuthTokenResponse? = null
        if (response.statusCode == HttpStatus.OK) {
            respData = JacksonHelper.deserializeFromString(response.body.ifNullOrBlank { "{}" }, OAuthTokenResponse::class, true)
            if(respData.error.code.isNullOrBlank()) {
                val body = respData.data
                val exp = body.expiresIn.toLongOrNull()
                val rtExp = body.refreshTokenExpiresIn.toLongOrNull()
                return PlatformAccessToken(
                    body.userId,
                    "",
                    body.asscessToken,
                    appId = options.appId,
                    appOpenId = body.userId,
                    expired = if (exp != null) Duration.ofSeconds(exp) else null,
                    refreshToken = body.refreshToken.ifBlank { null },
                    refreshTokenExpired = if (rtExp != null) Duration.ofSeconds(rtExp) else null
                )
            }
        }
        throwExchangeError(respData)
    }

    private fun throwExchangeError(body: Any?): Nothing {
        val error = SocialExchangeException(this.options.providerName).apply {
            if (body != null) {
                this.platformResponse = if(body is String) body else JacksonHelper.serializeAsString(body)
            }
        }
        logger.error(error.toString())

        throw error
    }

    //参考： https://opendocs.alipay.com/mini/introduce/aes
    override fun decryptTurelyPhoneNumber(encryptedData: String, token: PlatformAccessToken, iv: String?): String {
        val json = decryptData(encryptedData)
        val data = JacksonHelper.deserializeFromString(json, MobileResponse::class)
        if (!data.error.code.isNullOrBlank() && data.error.code != "10000") {
            this.throwExchangeError(data)
        }
        return data.mobile
    }

    private fun decryptData(encryptedData: String): String {

        //1. 获取验签和解密所需要的参数
        val openapiResult = JacksonHelper.deserializeMap(
            encryptedData.toByteArray(Charsets.UTF_8),
            String::class,
            String::class
        ).toMutableMap()

        val charset = Charset.forName(openapiResult["charset"] ?: "utf-8")
        val sign = openapiResult.remove("sign")
        val signType: String = openapiResult.remove("signType") ?: "RSA2"
        val content = openapiResult["response"]

        //如果密文的
        val isDataEncrypted = !content!!.startsWith("{")

        //2. 验签
        val signVeriKey = this.options.alipayPubKey //小程序对应的支付宝公钥
        val decryptKey = this.options.aesKey
        //如果是加密的报文则需要在密文的前后添加双引号
        val signContent = if (isDataEncrypted) {
            "\"" + content + "\"";
        } else ""

        try {
            if (signType == "RSA2") {
                RsaUtils.verifySHA256(signContent, sign!!, signVeriKey, charset)
            } else {
                RsaUtils.verifySHA1(signContent, sign!!, signVeriKey, charset)
            }
        } catch (e: RsaException) {
            throw BadSignatureException(platform = this.options.providerName)
        }

        //3. 解密
        return if (isDataEncrypted) {
            try {
                AesUtils.decrypt(content, decryptKey, charset)
            } catch (e: AesException) { //解密异常, 记录日志
                throw SocialDataDecryptionException("解密异常")
            }
        } else {
            content
        }
    }
}