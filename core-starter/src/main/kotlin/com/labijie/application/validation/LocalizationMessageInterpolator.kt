package com.labijie.application.validation

import jakarta.validation.MessageInterpolator
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-07
 */
class LocalizationMessageInterpolator(private val defaultInterpolator: MessageInterpolator) : MessageInterpolator {
    override fun interpolate(messageTemplate: String?, context: MessageInterpolator.Context?): String {
        return defaultInterpolator.interpolate(messageTemplate, context)
    }

    override fun interpolate(messageTemplate: String?, context: MessageInterpolator.Context?, locale: Locale?): String {
        return defaultInterpolator.interpolate(messageTemplate, context, locale)
    }
}