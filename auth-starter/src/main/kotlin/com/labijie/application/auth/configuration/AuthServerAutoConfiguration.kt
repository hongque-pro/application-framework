package com.labijie.application.auth.configuration

import com.labijie.application.auth.AuthErrorsRegistration
import com.labijie.application.auth.component.*
import com.labijie.application.auth.event.UserSignInEventListener
import com.labijie.application.auth.handler.OAuth2ErrorHandler
import com.labijie.application.identity.configuration.IdentityAutoConfiguration
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.Constants
import com.labijie.infra.oauth2.IClientDetailsServiceFactory
import com.labijie.infra.oauth2.IIdentityService
import com.labijie.infra.oauth2.configuration.OAuth2CustomizationAutoConfiguration
import com.labijie.infra.oauth2.error.IOAuth2ExceptionHandler
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(OAuth2CustomizationAutoConfiguration::class)
@AutoConfigureAfter(IdentityAutoConfiguration::class)
class AuthServerAutoConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {
        registry.antMatchers(
            "/account/register",
            "/account/verify",
            "/account/set-password",
        ).permitAll()
            .antMatchers("/user/current").authenticated()
    }


    @Bean
    fun authErrorsRegistration(): AuthErrorsRegistration {
        return AuthErrorsRegistration()
    }

    @Bean
    @ConditionalOnMissingBean(IIdentityService::class)
    fun defaultIdentityService(userService: IUserService): DefaultIdentityService {
        return DefaultIdentityService(userService)
    }

    @Bean
    @ConditionalOnMissingBean(ISignInPlatformDetection::class)
    fun defaultSignInPlatformDetection(): DefaultSignInPlatformDetection {
        return DefaultSignInPlatformDetection()
    }

    @Bean
    @ConditionalOnMissingBean(UserSignInEventListener::class)
    fun userSignInEventListener(
        signInPlatformDetection: ISignInPlatformDetection,
        userService: IUserService
    ): UserSignInEventListener {
        return UserSignInEventListener(signInPlatformDetection, userService)
    }


    @Bean
    @ConditionalOnMissingBean(IOAuth2ExceptionHandler::class)
    fun oauth2ErrorHandler(): OAuth2ErrorHandler {
        return OAuth2ErrorHandler()
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(IClientDetailsServiceFactory::class)
    protected class ClientDetailServiceAutoConfiguration {

        private fun authClientDetailsService(
            oauth2ClientService: IOAuth2ClientService
        ): AuthClientDetailsService {
            return AuthClientDetailsService(oauth2ClientService)
        }

        @Bean
        fun mybatisClientDetailsServiceFactory(oAuth2ClientService: IOAuth2ClientService): AuthClientDetailsServiceFactory {
            return AuthClientDetailsServiceFactory(authClientDetailsService(oAuth2ClientService))
        }
    }


}