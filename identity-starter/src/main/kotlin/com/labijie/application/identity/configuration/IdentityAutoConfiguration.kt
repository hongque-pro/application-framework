package com.labijie.application.identity.configuration

import com.labijie.application.annotation.ImportErrorDefinition
import com.labijie.application.identity.IdentityErrors
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.social.DefaultSocialUserGenerator
import com.labijie.application.identity.social.ISocialUserGenerator
import com.labijie.infra.orm.annotation.TableScan
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:28
 * @Description:
 */
@TableScan(basePackageClasses = [UserTable::class])
@EnableConfigurationProperties(IdentityProperties::class)
@Configuration(proxyBeanMethods = false)
@ImportErrorDefinition([IdentityErrors::class])
class IdentityAutoConfiguration {

    private val passwordEncoder by lazy {
        BCryptPasswordEncoder()
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