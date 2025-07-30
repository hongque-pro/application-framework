package com.labijie.application.email.mvc

import com.labijie.application.email.model.EmailVerificationSendRequest
import com.labijie.application.email.service.IEmailService
import com.labijie.application.exception.EmailAddressNotVerifiedException
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.VerificationCodeType
import com.labijie.application.model.VerificationToken
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.infra.oauth2.TwoFactorPrincipal
import jakarta.annotation.security.PermitAll
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@RestController
@RequestMapping("/mail")
@Validated
class EmailController(
    private val emailService: IEmailService,
    private val userService: IUserService,
) {
    @PermitAll
    @PutMapping("/verification-code")
    @HumanVerify
    fun send(@RequestBody @Valid request: EmailVerificationSendRequest): VerificationToken {
        return emailService.sendVerificationCode(request.to, request.type)
    }

    @PutMapping("/verification-code/to-me")
    fun sendToUser(
        @RequestParam(required = true) type: VerificationCodeType,
        principal: TwoFactorPrincipal
    ): VerificationToken {
        val userId = principal.userId.toLong()
        val user = userService.getUserById(userId) ?: throw UserNotFoundException()

        if (!user.emailConfirmed) {
            throw EmailAddressNotVerifiedException()
        }

        return emailService.sendVerificationCode(user.email, type)
    }
}