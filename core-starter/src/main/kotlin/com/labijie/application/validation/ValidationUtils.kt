package com.labijie.application.validation

import com.labijie.application.localeMessage
import org.springframework.context.i18n.LocaleContextHolder

/**
 * @author Anders Xiao
 * @date 2023-12-07
 */
internal object ValidationUtils {
    private val messageCodePattern = Regex("^\\{.+\\}\$")

    fun localeMessage(message: String, defaultMessage: String): String {
        return if(messageCodePattern.matches(message)){
            val code = message.trim('{', '}')
            localeMessage(defaultMessage, code, LocaleContextHolder.getLocale())
        }else {
            message
        }
    }
}