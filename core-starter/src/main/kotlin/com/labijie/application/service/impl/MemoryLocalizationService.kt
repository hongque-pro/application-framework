/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.service.impl

import com.labijie.application.model.LocalizationMessages
import com.labijie.application.service.ILocalizationService
import java.util.*


class MemoryLocalizationService : ILocalizationService {

    private val map = mutableMapOf<String, MutableMap<String, String>>()
    private val writeLock = Any()
    private fun Locale.getId() = this.toLanguageTag()

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

    override fun setMessage(code: String, message: String, locale: Locale, override: Boolean): Boolean {
        return synchronized(writeLock) {
            val values = map.getOrPut(code) { mutableMapOf() }
            values.set(code, message, override)
        }
    }

    override fun setMessages(properties: Properties, locale: Locale, override: Boolean): Int {
        var count = 0
        synchronized(writeLock) {
            properties.forEach {
                val success = setMessage(it.key.toString(), it.value.toString(), locale, override)
                if (success) {
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
            message.messages.forEach { code, message ->
                data.set(code, message, override)
            }
        }
    }

    override fun reload() {

    }
}