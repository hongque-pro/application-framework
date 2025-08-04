package com.labijie.application.web.controller

import com.labijie.application.web.controller.ApplicationControllerProperties.Companion.CONFIG_KEY
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Anders Xiao
 * @date 2025/8/4
 */
@ConfigurationProperties(CONFIG_KEY)
data class ApplicationControllerProperties(
    var application: Boolean = true,
    var file: Boolean = true,
    var captchaVerify: Boolean = false,
    var oneTimeCodeVerify: Boolean = false
) {
    companion object {
        const val CONFIG_KEY = "application.web.controllers"
    }
}