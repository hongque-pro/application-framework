package com.labijie.application.web.controller

import com.labijie.application.captcha.SpecCaptcha
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.crypto.DesUtils
import com.labijie.application.model.CaptchaResponseData
import jakarta.annotation.security.PermitAll
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
@PermitAll
@RestController
@Validated
@RequestMapping("/captcha")
class ImageCaptchaGenerationController(private val applicationProperties: ApplicationCoreProperties) {

    @GetMapping
    fun get(
        @RequestParam("w") width: Int? = null,
        @RequestParam("h") height: Int? = null,
        @RequestParam("l") len: Int? = null,
    ): CaptchaResponseData {
        val w = (width ?: 128).coerceAtLeast(100)
        val h = (height ?: 32).coerceAtLeast(32)
        val l = (len ?: 3).coerceAtLeast(4).coerceAtMost(12)

        val captcha = SpecCaptcha(w, h, l)

        val tokenStamp = DesUtils.generateToken(captcha.text().lowercase(), Duration.ofMinutes(2), applicationProperties.desSecret)

        return CaptchaResponseData(captcha.toBase64(), tokenStamp, captcha.mimeType, 120)
    }
}