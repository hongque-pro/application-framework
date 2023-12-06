package com.labijie.application.service

import org.slf4j.LoggerFactory
import org.springframework.context.*
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
class LocalizationMessageSource(private val localizationService: ILocalizationService) : MessageSource, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    companion object {
        val logger = LoggerFactory.getLogger(LocalizationMessageSource::class.java)
    }

    private val messageSource: MessageSource? by lazy {
        applicationContext.getBeanProvider(MessageSource::class.java).filter {
             it != this
        }.firstOrNull()
    }

    override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale): String? {
        var msg = localizationService.getMessage(code, locale)
        if(msg == null) {
            msg = messageSource?.getMessage(code, null, null, locale) ?: defaultMessage
            localizationService.setMessage(code, msg ?: "", locale, false)
            logger.info("New localization message added: $code (${locale.toLanguageTag()})")
        }
        if(msg == null) {
            return null
        }
        return if (args.isNullOrEmpty()) msg else String.format(msg, *args)
    }

    override fun getMessage(code: String, args: Array<out Any>?, locale: Locale): String {
        return getMessage(code, args, null, locale) ?: throw NoSuchMessageException(code, locale)
    }

    override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
        val args = resolvable.arguments
        resolvable.codes?.forEach {
            val msg = getMessage(it, args, null, locale)
            if(!msg.isNullOrBlank()){
                return@getMessage msg
            }
        }
        return resolvable.defaultMessage ?: throw NoSuchMessageException(resolvable.codes?.firstOrNull() ?: "", locale)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}