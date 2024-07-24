/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.service.impl

import com.labijie.application.findLocale
import com.labijie.application.getId
import com.labijie.application.model.LocalizationMessages
import com.labijie.application.service.ILocalizationService
import org.apache.commons.lang3.LocaleUtils
import java.util.*


class MemoryLocalizationService : ILocalizationService {

    private val map = mutableMapOf<String, MutableMap<String, String>>()
    private val writeLock = Any()
    private var defaultLocal: String? = null

    fun MutableMap<String, String>.set(key:String, value: String, override: Boolean): Boolean {
        if(!this.containsKey(key)){
            this.putIfAbsent(key, value)
            return true
        }
        if(override) {
            this[key] = value
            return true
        }
        return false
    }

    override fun allLocales(): List<Locale> {
        return map.keys.map {
            LocaleUtils.toLocale(it)
        }
    }

    override fun findSupportedLocale(locale: Locale): Locale {
        return findLocale(locale, allLocales()) ?: getDefault()
    }

    override fun setDefault(locale: Locale): Boolean {
        if(defaultLocal != locale.getId()) {
            defaultLocal = locale.getId()
            return true
        }
        return false
    }

    override fun getDefault(): Locale {
        return defaultLocal?.let {
            LocaleUtils.toLocale(it)
        } ?: Locale.US
    }

    override fun setMessage(code: String, message: String, locale: Locale, override: Boolean): Boolean {
        return synchronized(writeLock) {
            val values = map.getOrPut(code) { mutableMapOf() }
            values.set(code, message, override)
        }
    }

    override fun setMessages(messages: Map<String, String>, locale: Locale, override: Boolean): Int {
        var count = 0
        synchronized(writeLock) {
            val values = map.getOrPut(locale.getId()) { mutableMapOf() }
            messages.forEach {
                if(values.set(it.key, it.value, override)) {
                    count++
                }
            }
        }
        return count
    }

    override fun getMessage(code: String, locale: Locale): String? {
        val id = locale.getId()
        return map[id]?.get(code)
    }

    override fun getLocaleMessages(locale: Locale): LocalizationMessages {
        val messages = map.getOrPut(locale.getId()) { mutableMapOf() }
        return LocalizationMessages(locale, messages)
    }

    override fun saveLocaleMessages(message: LocalizationMessages, override: Boolean) {
        val id = message.locale.getId()
        synchronized(writeLock) {
            val data = map.getOrPut(id) { mutableMapOf() }
            message.messages.forEach { (code, message) ->
                data.set(code, message, override)
            }
        }
    }

    override fun reload() {

    }
}