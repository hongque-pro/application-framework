package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * @author Anders Xiao
 * @date 2023-12-03
 */
@ConfigurationProperties("application")
data class ApplicationCoreProperties(
    var desSecret: String = DEFAULT_DES_SECRET,
    var preloadLocales: String = "zh_CN,en_US",

    var localizationService: String = "memory",

    @NestedConfigurationProperty
    val file: FileSettings = FileSettings(),


) {
    companion object {
        private const val DEFAULT_DES_SECRET = "!QAZde#@W1122"

        val ApplicationCoreProperties.isDefaultDesSecret
            get() = desSecret == DEFAULT_DES_SECRET
    }
}