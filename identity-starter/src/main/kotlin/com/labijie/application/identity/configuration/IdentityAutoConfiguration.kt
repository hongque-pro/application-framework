package com.labijie.application.identity.configuration

import com.labijie.application.identity.IdentityErrorsRegistration
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.application.identity.service.IUserService
import com.labijie.application.identity.service.impl.DefaultOAuth2ClientService
import com.labijie.application.identity.service.impl.DefaultUserService
import com.labijie.application.identity.social.DefaultSocialUserGenerator
import com.labijie.application.identity.social.ISocialUserGenerator
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.orm.annotation.TableScan
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:28
 * @Description:
 */
@TableScan(basePackageClasses = [UserTable::class])
@EnableConfigurationProperties(IdentityProperties::class)
@Configuration(proxyBeanMethods = false)
class IdentityAutoConfiguration {

    private val passwordEncoder by lazy {
        BCryptPasswordEncoder()
    }

    @Bean
    fun identityErrorsRegistration(): IdentityErrorsRegistration {
        return IdentityErrorsRegistration()
    }



    @Bean
    @ConditionalOnMissingBean(PasswordEncoder::class)
    fun identityPasswordEncoder(): PasswordEncoder {
        return passwordEncoder
    }

    @Bean
    @ConditionalOnMissingBean(ISocialUserGenerator::class)
    fun defaultSocialUserGenerator(): DefaultSocialUserGenerator {
        return DefaultSocialUserGenerator()
    }
}