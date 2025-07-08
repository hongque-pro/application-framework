package com.labijie.application.web.controller

import com.labijie.application.captcha.SpecCaptcha
import com.labijie.application.captcha.base.Captcha
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.crypto.DesUtils
import com.labijie.application.model.CaptchaResponseData
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.toSimpleValue
import com.labijie.application.service.CaptchaHumanChecker
import com.labijie.application.web.getRealIp
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*
import java.time.Duration


/**
 * @author Anders Xiao
 * @date 2023-12-03
 */

@PermitAll
@RestController
@RequestMapping("/captcha")
class CaptchaController(
    private val applicationProperties: ApplicationCoreProperties,
    private val captchaHumanChecker: CaptchaHumanChecker
) {
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

        val tokenStamp = DesUtils.generateToken(captcha.text().lowercase(), Duration.ofMinutes(10), applicationProperties.desSecret)

        return CaptchaResponseData(captcha.toBase64(), tokenStamp, captcha.mimeType)
    }

    @RequestMapping("/verify", method = [RequestMethod.GET, RequestMethod.POST])
    fun verify(code: String, stamp: String, request: HttpServletRequest): SimpleValue<Boolean> {
        if(code.isBlank() || stamp.isBlank()) {
            return false.toSimpleValue()
        }
        val valid = captchaHumanChecker.check(code, stamp, request.getRealIp())
        return valid.toSimpleValue()
    }

}