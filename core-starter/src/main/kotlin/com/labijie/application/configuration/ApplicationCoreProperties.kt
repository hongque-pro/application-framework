package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * @author Anders Xiao
 * @date 2023-12-03
 */
@ConfigurationProperties("application")
class ApplicationCoreProperties {
    companion object {
        private const val DEFAULT_DES_SECRET = "!QAZde#@W1122"
    }

    var desSecret: String = DEFAULT_DES_SECRET
    var preloadLocales: String = "zh_CN,en_US"

    @NestedConfigurationProperty
    val file: FileSettings = FileSettings()

    val isDefaultDesSecret: Boolean
        get() = desSecret == DEFAULT_DES_SECRET
}