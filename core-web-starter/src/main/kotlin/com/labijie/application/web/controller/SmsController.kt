package com.labijie.application.web.controller

import com.labijie.application.component.IMessageService
import com.labijie.application.component.SmsToken
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.*
import com.labijie.infra.oauth2.TwoFactorPrincipal
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
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
): ApplicationContextAware {

    protected lateinit var springContext: ApplicationContext


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

    @PostMapping("/verify")
    fun verify(@NotBlank @Length(max=6) code: String, @NotBlank token: String): SimpleValue<Boolean> {
        messageService.verifySmsCode(code, token, true)
        return true.toSimpleValue()
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.springContext = applicationContext
    }
}