package com.labijie.application.sms.mvc

import com.labijie.application.component.IPhoneValidator
import com.labijie.application.component.impl.NationalPhoneValidator
import com.labijie.application.exception.PhoneNumberNotVerifiedException
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.VerificationCodeType
import com.labijie.application.model.OneTimeGenerationResult
import com.labijie.application.sms.model.SmsVerificationCodeSendRequest
import com.labijie.application.sms.service.ISmsService
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.infra.oauth2.TwoFactorPrincipal
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.security.PermitAll
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@RestController
@RequestMapping("/sms")
@Validated
class SmsController @Autowired constructor(
    private val userService: IUserService,
    private val messageService: ISmsService
) : ApplicationContextAware {

    private var springContext: ApplicationContext? = null

    private val phoneValidator by lazy {
        springContext?.getBeansOfType(IPhoneValidator::class.java)?.values?.firstOrNull() ?: NationalPhoneValidator()
    }

    @PermitAll
    @PutMapping("/totp")
    @HumanVerify
    fun send(@RequestBody @Valid request: SmsVerificationCodeSendRequest): OneTimeGenerationResult {
        phoneValidator.validate(request.dialingCode, request.phoneNumber, true)
        return messageService.sendVerificationCode(request.dialingCode, request.phoneNumber, request.type)
    }

    @PutMapping("/totp/me")
    fun sendToUser(
        @Parameter(required = true) type: VerificationCodeType,
        principal: TwoFactorPrincipal
    ): OneTimeGenerationResult {
        val userId = principal.userId.toLong()
        val user = userService.getUserById(userId) ?: throw UserNotFoundException()

        if(!user.phoneNumberConfirmed) {
            throw PhoneNumberNotVerifiedException()
        }

        return messageService.sendVerificationCode(user.phoneCountryCode, user.phoneNumber, type)
    }


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.springContext = applicationContext
    }
}