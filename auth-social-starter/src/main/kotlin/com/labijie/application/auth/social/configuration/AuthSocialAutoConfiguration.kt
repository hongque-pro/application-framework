package com.labijie.application.auth.social.configuration

import com.labijie.application.auth.configuration.ApplicationAuthServerAutoConfiguration
import com.labijie.application.auth.social.AuthSocialErrorsRegistration
import com.labijie.application.auth.social.providers.alipay.AlipayMiniOptions
import com.labijie.application.auth.social.providers.alipay.AlipayMiniProgramProvider
import com.labijie.application.auth.social.providers.wechat.WechatMiniOptions
import com.labijie.application.auth.social.providers.wechat.WechatMiniProgramProvider
import com.labijie.application.identity.configuration.IdentityAutoConfiguration
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.web.client.RestTemplate

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AuthSocialProperties::class)
@AutoConfigureBefore(ApplicationAuthServerAutoConfiguration::class)
@AutoConfigureAfter(IdentityAutoConfiguration::class)
class AuthSocialAutoConfiguration : IResourceAuthorizationConfigurer {
    companion object{
        const val PROVIDERS_CONFIG_PREFIX = "application.auth.social.providers"
    }


    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {

        registry.requestMatchers(
            "/oauth/social/register",
            "/oauth/social/login"
        ).permitAll()
    }

    @Bean
    fun authSocialErrorsRegistration(): AuthSocialErrorsRegistration {
        return AuthSocialErrorsRegistration()
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value= ["$PROVIDERS_CONFIG_PREFIX.mini-wechat.enabled"], matchIfMissing = true)
    @EnableConfigurationProperties(WechatMiniOptions::class)
    protected class WeChatMiniProgramConfiguration {

        @Bean
        fun wechatMiniProgramProvider(
            restTemplate: RestTemplate,
            wechatMiniOptions: WechatMiniOptions
        ): WechatMiniProgramProvider {
            return WechatMiniProgramProvider(restTemplate, wechatMiniOptions)
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value= ["$PROVIDERS_CONFIG_PREFIX.mini-alipay.enabled"], matchIfMissing = true)
    @EnableConfigurationProperties(AlipayMiniOptions::class)
    protected class AlipayMiniProgramConfiguration {

        @Bean
        fun alipayMiniProgramProvider(
            restTemplate: RestTemplate,
            alipayMiniOptions: AlipayMiniOptions
        ): AlipayMiniProgramProvider {
            return AlipayMiniProgramProvider(alipayMiniOptions, restTemplate)
        }
    }

}