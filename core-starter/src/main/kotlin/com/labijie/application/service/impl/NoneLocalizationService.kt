package com.labijie.application.service.impl

import com.labijie.application.model.LocalizationMessages
import com.labijie.application.service.ILocalizationService
import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-07
 */
class NoneLocalizationService : ILocalizationService {
    override fun allLocales(): List<Locale> {
        return listOf()
    }

    override fun findSupportedLocale(locale: Locale): Locale? {
        return null
    }

    override fun setDefault(locale: Locale): Boolean {
        return false
    }

    override fun getDefault(): Locale {
        return Locale.getDefault() ?: Locale.US
    }

    override fun setMessage(code: String, message: String, locale: Locale, override: Boolean): Boolean {
        return false
    }

    override fun setMessages(messages: Map<String, String>, locale: Locale, override: Boolean): Int {
        return 0
    }


    override fun getMessage(code: String, locale: Locale): String? {
        return null
    }
    override fun getLocaleMessages(locale: Locale): LocalizationMessages {
        return LocalizationMessages(LocaleContextHolder.getLocale())
    }

    override fun saveLocaleMessages(message: LocalizationMessages, override: Boolean) {

    }

    override fun reload() {

    }
}