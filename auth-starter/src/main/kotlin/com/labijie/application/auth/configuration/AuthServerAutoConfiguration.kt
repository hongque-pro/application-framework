package com.labijie.application.auth.configuration

import com.labijie.application.auth.AuthErrorsRegistration
import com.labijie.application.auth.component.*
import com.labijie.application.auth.data.mapper.OAuth2ClientDetailsMapper
import com.labijie.application.auth.data.mapper.RoleMapper
import com.labijie.application.auth.data.mapper.UserMapper
import com.labijie.application.auth.data.mapper.UserRoleMapper
import com.labijie.application.auth.event.UserSignInEventListener
import com.labijie.application.auth.handler.OAuth2ErrorHandler
import com.labijie.application.auth.service.IUserService
import com.labijie.application.auth.service.impl.DefaultUserService
import com.labijie.application.component.IMessageSender
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.oauth2.IClientDetailsServiceFactory
import com.labijie.infra.oauth2.IIdentityService
import com.labijie.infra.oauth2.configuration.OAuth2CustomizationAutoConfiguration
import com.labijie.infra.oauth2.error.IOAuth2ExceptionHandler
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.transaction.support.TransactionTemplate

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@AutoConfigureBefore(OAuth2CustomizationAutoConfiguration::class)
@EnableConfigurationProperties(AuthServerProperties::class)
class AuthServerAutoConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {
        registry.antMatchers(
                "/account/register",
                "/account/verify",
                "/account/set-password"
        ).permitAll()
        .antMatchers("/user/current").authenticated()
    }


    @Bean
    fun authErrorsRegistration(): AuthErrorsRegistration {
        return AuthErrorsRegistration()
    }

    @Bean
    @ConditionalOnMissingBean(IUserService::class)
    fun defaultUserService(
            authServerProperties: AuthServerProperties,
            idGenerator: IIdGenerator,
            messageSender: IMessageSender,
            cacheManager: ICacheManager,
            userMapper: UserMapper,
            userRoleMapper: UserRoleMapper,
            roleMapper: RoleMapper,
            transactionTemplate: TransactionTemplate
    ): DefaultUserService {
        return DefaultUserService(
                authServerProperties,
                idGenerator,
                messageSender,
                cacheManager,
                userMapper,
                userRoleMapper,
                roleMapper,
                transactionTemplate)
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
    fun userSignInEventListener(signInPlatformDetection: ISignInPlatformDetection, userService: IUserService): UserSignInEventListener {
        return UserSignInEventListener(signInPlatformDetection, userService)
    }


    @Bean
    @ConditionalOnMissingBean(IOAuth2ExceptionHandler::class)
    fun oauth2ErrorHandler(): OAuth2ErrorHandler {
        return OAuth2ErrorHandler()
    }

    @Configuration
    @ConditionalOnMissingBean(IClientDetailsServiceFactory::class)
    protected class ClientDetailServiceAutoConfiguration {

        @Bean
        fun mybatisClientDetailsService(
                authServerProperties: AuthServerProperties,
                cacheManager: ICacheManager,
                clientDetailsMapper: OAuth2ClientDetailsMapper): MybatisClientDetailsService {
            return MybatisClientDetailsService(authServerProperties, cacheManager, clientDetailsMapper)
        }

        @Bean
        fun mybatisClientDetailsServiceFactory(mybatisClientDetailsService: MybatisClientDetailsService): MybatisClientDetailsServiceFactory {
            return MybatisClientDetailsServiceFactory(mybatisClientDetailsService)
        }
    }


}