package com.labijie.application.model

import java.util.Locale

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
data class LocalizationMessages(
    var locale: Locale = Locale.SIMPLIFIED_CHINESE,
    val messages: Map<String, String> = mapOf()
)