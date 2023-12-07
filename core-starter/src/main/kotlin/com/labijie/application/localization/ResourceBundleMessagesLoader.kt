package com.labijie.application.localization

import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-07
 */
class ResourceBundleMessagesLoader : ReloadableResourceBundleMessageSource() {
    fun loadAllMessages(locale: Locale): Properties {
        val properties = getMergedProperties(locale)
        return properties.properties ?: Properties()
    }
}