package com.labijie.application.kaptcha.model

/**
 *
 * @author lishiwen
 * @date 20-8-21
 * @since JDK1.8
 */
data class ImageCaptchaEntry(
    val stamp: String,
    val img: String
)

data class ImageCaptchaVerify(
    val stamp: String,
    val captcha: String,
    val userKey: String
)
