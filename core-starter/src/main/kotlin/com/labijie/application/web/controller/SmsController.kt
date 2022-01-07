package com.labijie.application.web.controller

import com.labijie.application.component.IMessageSender
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.CaptchaType
import com.labijie.application.model.CaptchaValidationRequest
import com.labijie.application.model.SendSmsCaptchaParam
import com.labijie.application.model.SimpleValue
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.application.web.annotation.ResponseWrapped
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.utils.ShortId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-17
 */
@RestController
@ResponseWrapped
@RequestMapping("/sms")
@ConditionalOnProperty(value=["application.sms.controller-enabled"], matchIfMissing = true)
class SmsController {
    @Autowired
    private lateinit var messageSender: IMessageSender
    @Autowired
    private lateinit var userService: IUserService


    @HumanVerify
    @Validated
    @PostMapping("/captcha")
    fun sendSmsCaptcha(
        @RequestParam("p", required = false) numberOrUserId: String? = null,
        @RequestParam("t", required = false) captchaType: CaptchaType? = CaptchaType.General,
        @RequestParam("m", required = false) modifier: String? = null
    ): SimpleValue<String> {
        var number: String? = numberOrUserId
        var userId: String? = null
        if (number.isNullOrBlank()) {
            val user = userService.getUserById(OAuth2Utils.currentTwoFactorPrincipal().userId.toLong())
                ?: throw BadCredentialsException("User missed.")
            number = user.phoneNumber!!
            userId = user.id.toString()
        } else if (captchaType != CaptchaType.Register) {
            //除了注册，必须验证用户
            val user = userService.getUser(numberOrUserId.orEmpty())
                ?: throw BadCredentialsException("User missed.")
            number = user.phoneNumber!!
            userId = user.id.toString()
        }

        val stamp = ShortId.newId()
        val param = SendSmsCaptchaParam(number, modifier, captchaType ?: CaptchaType.General, stamp);
        messageSender.sendSmsCaptcha(param, true)
        return SimpleValue(stamp)
    }

    @PostMapping("/validate")
    fun validateCaptcha(
        @RequestParam("m", required = false) modifier: String? = null,
        @RequestBody @Validated request: CaptchaValidationRequest
    ): SimpleValue<Boolean> {
        val valid = messageSender.verifySmsCaptcha(request.captcha, request.stamp, modifier, true)
        return SimpleValue(valid)
    }
}