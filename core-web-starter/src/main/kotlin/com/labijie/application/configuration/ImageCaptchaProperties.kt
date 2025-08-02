package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Anders Xiao
 * @date 2025/8/2
 */
@ConfigurationProperties(prefix = "application.captcha.image")
class ImageCaptchaProperties {
    var enabled = false
}