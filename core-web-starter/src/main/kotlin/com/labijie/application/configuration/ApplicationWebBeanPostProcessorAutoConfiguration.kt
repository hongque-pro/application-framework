package com.labijie.application.configuration

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.HibernateValidatorConfiguration
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.core.Ordered
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor

/**
 * @author Anders Xiao
 * @date 2025/8/5
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class ApplicationWebBeanPostProcessorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Validator::class)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    fun validator(): Validator {
        val validatorFactory = Validation.byProvider(HibernateValidator::class.java)
            .configure()
            .addProperty(HibernateValidatorConfiguration.FAIL_FAST, "true")
            .buildValidatorFactory()
        return validatorFactory.validator
    }

    @Bean
    @ConditionalOnMissingBean(MethodValidationPostProcessor::class)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    fun methodValidationPostProcessor(validator: Validator): MethodValidationPostProcessor {
        val postProcessor = MethodValidationPostProcessor()
        postProcessor.setValidator(validator);
        return postProcessor
    }
}