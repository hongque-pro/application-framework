/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.configuration

import com.labijie.application.component.IEmailAddressValidator
import com.labijie.application.component.IPhoneValidator
import com.labijie.application.component.IUserNameValidator
import com.labijie.application.component.impl.NationalPhoneValidator
import com.labijie.application.component.impl.EmailAddressValidator
import com.labijie.application.component.impl.UsernameValidator
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered


@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
class ApplicationDefaultsAutoConfiguration {

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
}