package com.labijie.application.auth.social.providers.wechat

import com.labijie.application.auth.social.SocialAuthOptions
import com.labijie.application.auth.social.configuration.AuthSocialAutoConfiguration.Companion.PROVIDERS_CONFIG_PREFIX
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
@ConfigurationProperties("$PROVIDERS_CONFIG_PREFIX.mini-wechat")
class WechatMiniOptions : SocialAuthOptions() {
    companion object{
        const val ProviderName = "mini-wechat"
    }

    override val multiOpenId: Boolean
        get() = this.useUnionId

    var useUnionId:Boolean = false

    init {
        this.providerName = ProviderName
        this.exchageUrl = "https://api.weixin.qq.com/sns/jscode2session"
    }
}