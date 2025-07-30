/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.configuration

import com.labijie.application.component.*
import com.labijie.application.component.impl.*
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered


@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 10)
class ApplicationValidatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IPhoneValidator::class)
    fun phoneValidator(): NationalPhoneValidator {
        return NationalPhoneValidator()
    }

    @Bean
    @ConditionalOnMissingBean(IEmailAddressValidator::class)
    fun emailAddressValidator(): EmailAddressValidator {
        return EmailAddressValidator()
    }

    @Bean
    @ConditionalOnMissingBean(IUserNameValidator::class)
    fun usernameValidator(): UsernameValidator {
        return UsernameValidator()
    }

    @Bean
    @ConditionalOnMissingBean(IStrongPasswordValidator::class)
    fun strongPasswordValidator(): StrongPasswordValidator {
        return StrongPasswordValidator()
    }

    @Bean
    @ConditionalOnMissingBean(IDisplayNameValidator::class)
    fun displayNameValidator(): DisplayNameValidator {
        return DisplayNameValidator()
    }
}