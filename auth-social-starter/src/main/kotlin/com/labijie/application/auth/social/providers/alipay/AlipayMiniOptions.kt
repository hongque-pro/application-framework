package com.labijie.application.auth.social.providers.alipay

import com.labijie.application.auth.social.SocialAuthOptions
import com.labijie.application.auth.social.configuration.AuthSocialAutoConfiguration.Companion.PROVIDERS_CONFIG_PREFIX
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
@ConfigurationProperties("$PROVIDERS_CONFIG_PREFIX.mini-alipay")
class AlipayMiniOptions : SocialAuthOptions() {
    override val multiOpenId: Boolean
        get() = false

    companion object{
        const val ProviderName = "mini-alipay"
    }

    init {
        this.providerName = ProviderName
        //https://opendocs.alipay.com/mini/introduce/authcode
        this.exchageUrl = "https://openapi.alipay.com/gateway.do"
    }

    override val isSandbox: Boolean
        get() = this.exchageUrl.lowercase() == "https://openapi.alipaydev.com/gateway.do"

    var alipayPubKey: String = ""
    var aesKey: String = ""
}