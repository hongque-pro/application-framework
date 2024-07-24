package com.labijie.application.service

import com.labijie.application.model.LocalizationMessages
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
interface ILocalizationService {
    fun allLocales(): List<Locale>

    fun findSupportedLocale(locale: Locale): Locale?

    fun setDefault(locale: Locale): Boolean

    fun getDefault(): Locale

    fun setMessage(code: String, message: String, locale: Locale, override: Boolean = false): Boolean

    fun setMessages(messages: Map<String, String>, locale: Locale, override: Boolean = false): Int

    fun getMessage(code: String, locale: Locale): String?

    fun getLocaleMessages(locale: Locale) : LocalizationMessages
    fun saveLocaleMessages(message: LocalizationMessages, override: Boolean = true)

    fun reload()
}


fun ILocalizationService.setMessages(properties: Properties, locale: Locale, override: Boolean = false): Int {
    val map = mutableMapOf<String, String>()
    properties.forEach {
        val code = it.key.toString()
        val message = it.value?.toString()
        if(code.isNotBlank() && !message.isNullOrBlank()) {
            map.putIfAbsent(code, message)
        }
    }
    return this.setMessages(map, locale, override)
}