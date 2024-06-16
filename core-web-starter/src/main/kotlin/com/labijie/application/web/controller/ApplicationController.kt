package com.labijie.application.web.controller

import com.labijie.application.IErrorRegistry
import com.labijie.application.service.ILocalizationService
import com.labijie.infra.json.JacksonHelper
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
@PermitAll
@RestController
@RequestMapping("/application/public")
class ApplicationController : ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    enum class LocalizationMessageFormat {
        Json,
        Text
    }

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
        if(errorRegistry.isLocalizationEnabled()) {
            val messages = errorRegistry.errorMessageCodes.toSortedMap().map {
                it.key to (messageSource.getMessage(it.value, null, locale) ?: "")
            }
            return messages.toMap()
        }else {
            return errorRegistry.defaultMessages
        }
    }

    @GetMapping("/locale-messages")
    @ResponseBody
    fun localeMessages(
        @RequestParam locale: Locale? = null,
        @RequestParam(
            defaultValue = "text",
            required = false
        ) format: LocalizationMessageFormat = LocalizationMessageFormat.Text,
        httpServletResponse: HttpServletResponse
    ) {
        httpServletResponse.characterEncoding = Charsets.UTF_8.name()

        val l = locale ?: LocaleContextHolder.getLocale()
        val messages = localizationService.getLocaleMessages(l)
        if (format == LocalizationMessageFormat.Text) {
            httpServletResponse.characterEncoding = Charsets.UTF_8.name()
            httpServletResponse.contentType = MediaType.TEXT_PLAIN_VALUE
            val b = StringBuilder()
            messages.messages.forEach {
                b.appendLine("${it.key}=${it.value}")
            }
            httpServletResponse.outputStream.write(b.toString().toByteArray(Charsets.UTF_8))
        } else {
            httpServletResponse.contentType = MediaType.APPLICATION_JSON_VALUE
            httpServletResponse.outputStream.write(JacksonHelper.serialize(messages, true))
        }
    }


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}