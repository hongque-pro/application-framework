package com.labijie.application.web.controller

import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.crypto.DesUtils
import com.labijie.application.model.CaptchaResponseData
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.toSimpleValue
import com.labijie.application.service.CaptchaHumanChecker
import com.labijie.application.web.getRealIp
import com.labijie.application.web.interceptor.HumanVerifyInterceptor
import io.springboot.captcha.SpecCaptcha
import io.springboot.captcha.base.Captcha
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import java.time.Duration


/**
 * @author Anders Xiao
 * @date 2023-12-03
 */

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
        val captcha = getCaptcha(width, height, len)

        val tokenStamp = DesUtils.generateToken(captcha.text().lowercase(), Duration.ofMinutes(10), applicationProperties.desSecret)

        return CaptchaResponseData(captcha.toBase64(), tokenStamp, captcha.contentType)
    }

    @RequestMapping("/verify", method = [RequestMethod.GET, RequestMethod.POST])
    fun verify(code: String, stamp: String, request: HttpServletRequest): SimpleValue<Boolean> {
        if(code.isBlank() || stamp.isBlank()) {
            return false.toSimpleValue()
        }
        val valid = captchaHumanChecker.check(code, stamp, request.getRealIp())
        return valid.toSimpleValue()
    }

    private fun getCaptcha(width: Int?, height: Int?, length: Int?): Captcha {
        val w = (width ?: 128).coerceAtLeast(100)
        val h = (height ?: 32).coerceAtLeast(32)
        val l = (length ?: 3).coerceAtLeast(4).coerceAtMost(12)
        return SpecCaptcha(w, h, l)
    }

}