package com.labijie.application.configuration

import com.labijie.application.service.ILocalizationService
import com.labijie.application.localization.LocalizationMessageSource
import com.labijie.application.localization.ResourceBundleMessagesLoader
import com.labijie.application.service.impl.LocalizationService
import com.labijie.application.service.impl.NoneLocalizationService
import com.labijie.infra.isProduction
import com.labijie.infra.utils.logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.autoconfigure.context.MessageSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
@AutoConfigureBefore(MessageSourceAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
class LocalizationAutoConfiguration {

    @Bean
    @Lazy
    @ConditionalOnMissingBean(ILocalizationService::class)
    fun localizationService(
        environment: Environment,
        @Autowired(required = false) transactionTemplate: TransactionTemplate?) : ILocalizationService {
        return if(transactionTemplate != null) {
            LocalizationService(transactionTemplate)
        }else {
            //just for unit test
            if(environment.isProduction) {
                throw NoSuchBeanDefinitionException(TransactionTemplate::class.java)
            }
            NoneLocalizationService()
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    fun messageSourceProperties(): MessageSourceProperties {
        return MessageSourceProperties()
    }

    fun createSpringSource(properties: MessageSourceProperties): ResourceBundleMessagesLoader {
        val messageSource = ResourceBundleMessagesLoader()
        val baseNames: MutableSet<String> =
        if (StringUtils.hasText(properties.basename)) {
            StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(properties.basename))

        }else {
            mutableSetOf()
        }
        baseNames.add("classpath:org/springframework/security/messages")
        baseNames.add("classpath:org/hibernate/validator/ValidationMessages")
        baseNames.add("classpath:com/labijie/application/messages")

        messageSource.basenameSet.addAll(baseNames.toTypedArray())
        messageSource.setDefaultEncoding(properties.encoding?.name() ?: StandardCharsets.UTF_8.name())
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale)
        val cacheDuration = properties.cacheDuration
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis())
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat)
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage)
        return messageSource
    }

    @Bean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    fun localizationMessageSource(properties: MessageSourceProperties) : MessageSource {

        val springSource = createSpringSource(properties)
        return LocalizationMessageSource(springSource)
    }
}