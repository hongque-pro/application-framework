package com.labijie.application.configuration

import com.labijie.application.service.ILocalizationService
import com.labijie.application.localization.LocalizationMessageSource
import com.labijie.application.localization.ResourceBundleMessagesLoader
import com.labijie.application.service.impl.JdbcLocalizationService
import com.labijie.application.service.impl.NoneLocalizationService
import com.labijie.caching.ICacheManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.autoconfigure.context.MessageSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.core.env.Environment
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import javax.sql.DataSource

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
        @Autowired(required = false) dataSource: DataSource?,
        @Autowired(required = false) transactionTemplate: TransactionTemplate?) : ILocalizationService {
        return if(transactionTemplate != null && dataSource != null) {
            JdbcLocalizationService(transactionTemplate)
        }else {
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
    @ConditionalOnBean(ILocalizationService::class)
    fun localizationMessageSource(properties: MessageSourceProperties) : MessageSource {

        val springSource = createSpringSource(properties)
        return LocalizationMessageSource(springSource)
    }
}