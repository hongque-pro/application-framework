/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.identity.configuration

import com.labijie.application.identity.component.ICustomUserDataPersistence
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.application.identity.service.IUserService
import com.labijie.application.identity.service.impl.DefaultOAuth2ClientService
import com.labijie.application.identity.service.impl.DefaultUserService
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.cglib.core.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource


@ConditionalOnBean(DataSource::class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration::class)
class IdentityJdbcServiceAutoConfiguration {
    @Bean
    @ConditionalOnBean(DataSource::class)
    @ConditionalOnMissingBean(IOAuth2ClientService::class)
    fun oath2ClientService(
        cacheManager: ICacheManager,
        identityProperties: IdentityProperties,
        transactionTemplate: TransactionTemplate
    ): DefaultOAuth2ClientService {
        return DefaultOAuth2ClientService(transactionTemplate, identityProperties, cacheManager)
    }

    @Bean
    @ConditionalOnBean(DataSource::class)
    @ConditionalOnMissingBean(IUserService::class)
    fun defaultUserService(
        passwordEncoder: PasswordEncoder,
        identityProperties: IdentityProperties,
        idGenerator: IIdGenerator,
        cacheManager: ICacheManager,
        transactionTemplate: TransactionTemplate,
        @Autowired(required = false)
        customUserDataPersistence: ICustomUserDataPersistence? = null
    ): DefaultUserService {
        return DefaultUserService(
            identityProperties,
            idGenerator,
            passwordEncoder,
            cacheManager,
            transactionTemplate,
            customUserDataPersistence
        )
    }
}