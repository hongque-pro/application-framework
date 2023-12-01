package com.labijie.application.auth.configuration

import com.labijie.application.auth.AuthErrorsRegistration
import com.labijie.application.auth.component.DefaultIdentityService
import com.labijie.application.auth.component.DefaultSignInPlatformDetection
import com.labijie.application.auth.component.ISignInPlatformDetection
import com.labijie.application.auth.event.UserSignInEventListener
import com.labijie.application.identity.configuration.IdentityAutoConfiguration
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.IIdentityService
import com.labijie.infra.oauth2.configuration.OAuth2DependenciesAutoConfiguration
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(IdentityAutoConfiguration::class)
@AutoConfigureBefore(OAuth2DependenciesAutoConfiguration::class)
class AuthServerAutoConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        registry.requestMatchers(
            "/account/register",
            "/account/verify",
            "/account/set-password",
        ).permitAll()
    }

//    @Bean
//    @ConditionalOnMissingBean(RegisteredClientRepository::class)
//    fun defaultClientRepository(clientService: IOAuth2ClientService): DefaultClientRepository {
//        return DefaultClientRepository(clientService)
//    }


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



//    @Bean
//    @ConditionalOnMissingBean(IOAuth2ExceptionHandler::class)
//    fun oauth2ErrorHandler(): OAuth2ErrorHandler {
//        return OAuth2ErrorHandler()
//    }



}