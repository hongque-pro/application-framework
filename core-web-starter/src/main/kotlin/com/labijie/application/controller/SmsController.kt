package com.labijie.application.controller

import com.labijie.application.component.IMessageService
import com.labijie.application.component.SmsToken
import com.labijie.application.component.impl.NoneMessageService
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.*
import com.labijie.infra.oauth2.TwoFactorPrincipal
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * @author Anders Xiao
 * @date 2023-12-01
 */
@RestController
@RequestMapping("/sms")
class SmsController @Autowired constructor(
    private val userService: IUserService,
    private val messageService: IMessageService
) {

    @PostMapping("/send")
    fun send(@RequestBody @Validated request: SmsSendRequest): SmsToken {
        return messageService.sendSmsCode(request.phoneNumber, request.type)
    }

    @PostMapping("/send-user")
    fun sendToUser(@RequestBody @Validated request: UserSmsSendRequest, @Parameter(hidden = true) principal: TwoFactorPrincipal): SmsToken {
        val userId = principal.userId.toLong()
        val user = userService.getUserById(userId) ?: throw UserNotFoundException()

        return messageService.sendSmsCode(user.phoneNumber, request.type)
    }

    @RequestMapping("/verify", method = [RequestMethod.POST])
    fun verify(@NotBlank(message = "短信验证码不能为空") code: String, @NotBlank(message = "令牌不能为空") token: String): SimpleValue<Boolean> {
        messageService.verifySmsCode(code, token, true)
        return true.toSimpleValue()
    }
}