package com.labijie.application.auth.configuration

import com.labijie.application.annotation.ImportErrorDefinition
import com.labijie.application.auth.AuthErrors
import com.labijie.application.auth.component.*
import com.labijie.application.auth.event.UserSignInEventListener
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.identity.configuration.IdentityAutoConfiguration
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.IIdentityService
import com.labijie.infra.oauth2.client.IOidcLoginHandler
import com.labijie.infra.oauth2.configuration.OAuth2DependenciesAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(IdentityAutoConfiguration::class)
@AutoConfigureBefore(OAuth2DependenciesAutoConfiguration::class)
@EnableConfigurationProperties(
    DefaultUserCreationProperties::class,
    AuthProperties::class
)
@ImportErrorDefinition([AuthErrors::class])
class PreAuthServerAutoConfiguration {

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


    @Bean
    @ConditionalOnProperty(
        prefix = "infra.oauth2",
        name = ["client-repository"],
        havingValue = "jdbc",
        matchIfMissing = true
    )

    @ConditionalOnMissingBean(RegisteredClientRepository::class)
    fun defaultClientRepository(clientService: IOAuth2ClientService): DefaultServerClientRepository {
        return DefaultServerClientRepository(clientService)
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "application.auth.default-user-creation",
        name = ["enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    @ConditionalOnMissingBean(DefaultUserInitializer::class)
    fun defaultUserInitializer(
        properties: DefaultUserCreationProperties,
    ): DefaultUserInitializer {
        return DefaultUserInitializer(properties)
    }

    @Bean
    @ConditionalOnMissingBean(IOidcLoginHandler::class)
    fun defaultOidcLoginHandler(oauth2ClientUserService: IOAuth2ClientUserService): DefaultOidcLoginHandler {
        return DefaultOidcLoginHandler(oauth2ClientUserService)
    }
}