package com.labijie.application.hcaptcha.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
@ConfigurationProperties(prefix = "application.hcaptcha")
class HCaptchaProperties {

    companion object {
        const val DEFAULT_ENDPOINT = "https://api.hcaptcha.com/siteverify"
    }
    var enabled: Boolean = true
    var endpoint: String = DEFAULT_ENDPOINT
    var secret: String = ""
}