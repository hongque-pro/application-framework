package com.labijie.application.web.controller

import com.labijie.application.IErrorRegistry
import com.labijie.application.model.LocalizationMessages
import com.labijie.application.service.ILocalizationService
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
@RestController("/application")
class ApplicationController: ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    private val errorRegistry by lazy {
        applicationContext.getBean(IErrorRegistry::class.java)
    }

    private val messageSource by lazy {
        applicationContext.getBean(MessageSource::class.java)
    }

    private val localizationService by lazy {
        applicationContext.getBean(ILocalizationService::class.java)
    }


    @GetMapping("/errors")
    fun errors(): Map<String, String> {
        val locale = LocaleContextHolder.getLocale()
        val mesages = errorRegistry.errorMessages.map {
            it.key to  (messageSource.getMessage(it.value.code, null, it.value.defaultMessage, locale) ?: "")
        }
        return mesages.toMap()
    }

    @GetMapping("/locale-messages")
    fun localeMessages(): LocalizationMessages {
        val locale = LocaleContextHolder.getLocale()
        return localizationService.getLocaleMessages(locale)
    }


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}