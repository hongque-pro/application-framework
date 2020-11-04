package com.labijie.application.auth.social

import com.labijie.application.SpringContext
import com.labijie.application.auth.social.exception.NoneOpenIdException
import com.labijie.application.auth.social.exception.NoneSocialUserException
import com.labijie.application.auth.social.providers.alipay.AlipayMiniOptions
import com.labijie.application.auth.social.providers.wechat.WechatMiniOptions
import com.labijie.application.auth.social.service.ISocialUserService
import com.labijie.infra.oauth2.TwoFactorPrincipal
import com.labijie.infra.oauth2.hasAttachedFiledValue
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer


val TwoFactorPrincipal.loginProvider:String
get() = this.attachedFields.getOrDefault(OAuth2SocialConstants.LoginProviderFieldName, "")

val TwoFactorPrincipal.loginProviderKey:String
    get() = this.attachedFields.getOrDefault(OAuth2SocialConstants.LoginProviderKeyFieldName, "")

fun TwoFactorPrincipal.getOpenId(appId:String, throwIfNoOpenId: Boolean = true):String {
    val svc = SpringContext.current.getBean(ISocialUserService::class.java)
    if(this.loginProvider.isBlank()){
        throw NoneSocialUserException()
    }
    val openId = svc.getOpenId(this.userId.toLong(), this.loginProvider, this.loginProvider)
    if(openId.isNullOrBlank() && throwIfNoOpenId){
        throw NoneOpenIdException(appId, this.loginProvider)
    }
    return openId.orEmpty()
}

val TwoFactorPrincipal.isWechatMiniUser: Boolean
get()= (this.loginProvider == WechatMiniOptions.ProviderName)

val TwoFactorPrincipal.isAlipayMiniUser: Boolean
    get()= (this.loginProvider == AlipayMiniOptions.ProviderName)


fun ExpressionUrlAuthorizationConfigurer<*>.AuthorizedUrl.wechatMiniOnly(): ExpressionUrlAuthorizationConfigurer<*>.ExpressionInterceptUrlRegistry {
    return this.hasAttachedFiledValue(OAuth2SocialConstants.LoginProviderFieldName, WechatMiniOptions.ProviderName)
}

fun ExpressionUrlAuthorizationConfigurer<*>.AuthorizedUrl.alipayMiniOnly(): ExpressionUrlAuthorizationConfigurer<*>.ExpressionInterceptUrlRegistry {
    return this.hasAttachedFiledValue(OAuth2SocialConstants.LoginProviderFieldName, AlipayMiniOptions.ProviderName)
}