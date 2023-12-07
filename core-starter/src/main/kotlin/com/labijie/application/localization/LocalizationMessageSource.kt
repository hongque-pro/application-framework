package com.labijie.application.localization

import com.labijie.application.service.ILocalizationService
import org.slf4j.LoggerFactory
import org.springframework.context.*
import org.springframework.context.support.MessageSourceAccessor
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
class LocalizationMessageSource(private val loader: ResourceBundleMessagesLoader) : MessageSource, ApplicationContextAware {


    companion object {
        private val logger = LoggerFactory.getLogger(LocalizationMessageSource::class.java)
    }

    private lateinit var applicationContext: ApplicationContext
    private var localizationService: ILocalizationService? = null

    fun preloadMessages(vararg locales: Locale) {
        localizationService?.let {
            svc->
            locales.forEach {
                val properties = loader.loadAllMessages(it)
                svc.setMessages(properties, it)
            }
        }
    }

    fun loadResourceBundle(vararg paths: String) {
        val set = loader.basenameSet
        set.addAll(paths)
    }

    private fun Collection<MessageSource>.getMessage(code: String, locale: Locale): String? {
        this.forEach {
            val msg = it.getMessage(code, null, null, locale)
            if(msg != null){
                return@getMessage msg
            }
        }
        return null
    }


    override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale): String? {
        val svc = this.localizationService ?: return defaultMessage
        var msg = svc.getMessage(code, locale)
        if(msg == null) {
            msg = (loader as MessageSource).getMessage(code, null, defaultMessage, locale)
            svc.setMessage(code, msg ?: "", locale, false)
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
        this.localizationService = applicationContext.getBeanProvider(ILocalizationService::class.java).ifAvailable
    }
}
