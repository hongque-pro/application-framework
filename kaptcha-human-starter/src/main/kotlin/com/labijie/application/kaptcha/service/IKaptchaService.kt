package com.labijie.application.kaptcha.service

import com.labijie.application.kaptcha.model.ImageCaptchaEntry
import com.labijie.application.kaptcha.model.ImageCaptchaVerify


interface IKaptchaService {

    fun genImageCaptcha(userKey: String): ImageCaptchaEntry

    fun validateImageCaptcha(param: ImageCaptchaVerify): Boolean
}
