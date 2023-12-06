package com.labijie.application.service

import com.labijie.application.model.LocalizationMessages
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
interface ILocalizationService {
    fun setMessage(code: String, message: String, locale: Locale, override: Boolean = false)
    fun getMessage(code: String, locale: Locale): String?

    fun getLocaleMessages(locale: Locale, includeBlank: Boolean = true) : LocalizationMessages
    fun saveLocaleMessages(message: LocalizationMessages, override: Boolean = true)
}