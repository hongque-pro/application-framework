package com.labijie.application.identity.configuration

import com.labijie.application.component.IMessageSender
import com.labijie.application.identity.data.mapper.*
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.application.identity.service.IUserService
import com.labijie.application.identity.service.impl.DefaultOAuth2ClientService
import com.labijie.application.identity.service.impl.DefaultUserService
import com.labijie.application.identity.social.DefaultSocialUserGenerator
import com.labijie.application.identity.social.ISocialUserGenerator
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:28
 * @Description:
 */
@AutoConfigureAfter(MybatisAutoConfiguration::class)
@MapperScan(basePackageClasses = [UserMapper::class])
@EnableConfigurationProperties(IdentityProperties::class)
@Configuration(proxyBeanMethods = false)
class IdentityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IOAuth2ClientService::class)
    fun oath2ClientService(
            cacheManager: ICacheManager,
            identityProperties: IdentityProperties,
            oAuth2ClientDetailsMapper: OAuth2ClientDetailsMapper,
            transactionTemplate: TransactionTemplate): DefaultOAuth2ClientService {
        return DefaultOAuth2ClientService(transactionTemplate, identityProperties, cacheManager, oAuth2ClientDetailsMapper)
    }


    @Bean
    @ConditionalOnMissingBean(IUserService::class)
    fun defaultUserService(
            identityProperties: IdentityProperties,
            idGenerator: IIdGenerator,
            messageSender: IMessageSender,
            cacheManager: ICacheManager,
            userMapper: UserMapper,
            userRoleMapper: UserRoleMapper,
            roleMapper: RoleMapper,
            userLoginMapper: UserLoginMapper,
            userOpenIdMapper: UserOpenIdMapper,
            transactionTemplate: TransactionTemplate
    ): DefaultUserService {
        return DefaultUserService(
                identityProperties,
                idGenerator,
                messageSender,
                cacheManager,
                userMapper,
                userRoleMapper,
                roleMapper,
                userLoginMapper,
                userOpenIdMapper,
                transactionTemplate)
    }

    @Bean
    @ConditionalOnMissingBean(ISocialUserGenerator::class)
    fun defaultSocialUserGenerator(): DefaultSocialUserGenerator {
        return DefaultSocialUserGenerator()
    }
}