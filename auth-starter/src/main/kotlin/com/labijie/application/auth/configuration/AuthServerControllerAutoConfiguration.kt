package com.labijie.application.auth.configuration

import com.labijie.application.auth.controller.AccountController
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.configuration.OAuth2ServerAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(OAuth2ServerAutoConfiguration::class)
class AuthServerControllerAutoConfiguration : WebMvcConfigurer{

    @ConditionalOnBean(TwoFactorSignInHelper::class)
    @Import(AccountController::class)
    protected class AccountControllerImport

//    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
//        super.extendMessageConverters(converters)
//        val index = converters.indexOfFirst {
//            it is OAuth2AccessTokenResponseHttpMessageConverter
//        }
//        if (index < 0) {
//            converters.add(0, OAuth2AccessTokenResponseHttpMessageConverter())
//        }
//    }
}