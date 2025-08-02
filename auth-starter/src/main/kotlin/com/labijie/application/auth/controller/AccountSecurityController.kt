package com.labijie.application.auth.controller

import com.labijie.application.auth.model.*
import com.labijie.application.auth.validateUser
import com.labijie.application.exception.InvalidOneTimeCodeException
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.getOneTimeCodeInRequest
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.exception.InvalidPasswordException
import com.labijie.application.identity.isNullEmail
import com.labijie.application.identity.isNullPhoneNumber
import com.labijie.application.identity.service.IUserService
import com.labijie.application.maskEmail
import com.labijie.application.maskPhone
import com.labijie.application.model.BindingStatus
import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.UpdateResult
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.web.annotation.OneTimeCodeVerify
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.oauth2.TwoFactorPrincipal
import com.labijie.infra.utils.logger
import io.swagger.v3.oas.annotations.Operation
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */

@RestController
@RequestMapping("/account/security")
@Validated
class AccountSecurityController @Autowired constructor(
    private val userService: IUserService,
    private val oneTimeCodeService: IOneTimeCodeService,
) {

    init {
        logger.info("AccountController loaded")
    }


    private fun Boolean?.toBindingStatus(): BindingStatus {
        return if (this == true) BindingStatus.Confirmed else BindingStatus.UnConfirmed
    }

    @GetMapping("/info")
    fun securityInfo(): SecurityView {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        val view = userService.getUserById(userId)?.let {
            securityView(it)
        }
        return view ?: SecurityView()
    }

    /**
     * 找回密码第一步，验证身份
     */
    @PermitAll
    @PostMapping("/verify-user")
    fun verify(@RequestParam("identifier", required = false) @NotBlank identifier: String): UserVerifyResult {
        val user = userService.getUser(identifier) ?: throw UserNotFoundException(identifier)
//        val type = when (identifier) {
//            "${user.phoneCountryCode}${user.phoneNumber}" -> UserIdentifierType.Phone
//            user.email -> UserIdentifierType.Email
//            user.userName -> UserIdentifierType.UserName
//            else -> UserIdentifierType.Unknown
//        }

        return UserVerifyResult(identifier, securityView(user))
    }

    /**
     * 找回密码（需要验证短信验证码）
     */
    @PermitAll
    @OneTimeCodeVerify
    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody @Validated request: SetPasswordRequest,
        codeInput: OneTimeCodeTarget
    ): SimpleValue<Boolean> {

        val u = userService.getUser(request.identifier) ?: throw UserNotFoundException()
        codeInput.validateUser(u, throwInfInvalid = true)

        val succeed = userService.resetPassword(u.id, request.password)
        return SimpleValue(succeed)
    }


    private fun securityView(user: User): SecurityView {
        return SecurityView().apply {
            this.email = if (user.isNullEmail) "" else user.email.maskEmail()
            this.emailStatus = if (user.isNullEmail) BindingStatus.Unbound else user.emailConfirmed.toBindingStatus()

            this.phoneNumber = user.phoneNumber.maskPhone()
            this.dialingCode = user.phoneCountryCode

            this.phoneStatus =
                if (user.isNullPhoneNumber) BindingStatus.Unbound else user.phoneNumberConfirmed.toBindingStatus()
        }
    }


    fun checkPasswordOrOneTimeCode(
        httpRequest: HttpServletRequest,
        user: User,
        password: String?
    ) {
        if(password.isNullOrBlank()) {
            val code = httpRequest.getOneTimeCodeInRequest() ?: throw InvalidOneTimeCodeException()
            val r = oneTimeCodeService.verifyCode(code.code, code.stamp, throwIfInvalid = true)
            val source = r.input ?: throw InvalidOneTimeCodeException()
            source.validateUser(user)
        }else {
            if (!userService.validatePassword(user, password)) {
                throw InvalidPasswordException()
            }
        }
    }

    /**
     * 变更新手机号
     */
    @PostMapping("/change-phone")
    @Operation(description = "If the password is empty, `TOTP` verification will be required.")
    fun changePhoneNumber(
        @RequestBody @Valid request: ChangePhoneRequest,
        twoFactorPrincipal: TwoFactorPrincipal,
        httpRequest: HttpServletRequest
    ): UpdateResult<String> {

        val userId = twoFactorPrincipal.userId.toLong()
        val u = userService.getUserById(userId) ?: throw UserNotFoundException()
        checkPasswordOrOneTimeCode(httpRequest, u, request.password)

        val newPhoneNumber = "${request.newPhone.dialingCode}${request.newPhone.phoneNumber}"

        return request.newPhone.let {
            val verify = it.verify ?: throw InvalidOneTimeCodeException()

            oneTimeCodeService.verifyCode(
                verify.code,
                verify.stamp,
                channel = OneTimeCodeTarget.Channel.Phone,
                newPhoneNumber,
                throwIfInvalid = true)

            val success = userService.changePhone(userId, it.dialingCode, it.phoneNumber, true)
            UpdateResult(it.phoneNumber.maskPhone(), success)
        }



    }

    @PostMapping("/change-email")
    @Operation(description = "If the password is empty, `TOTP` verification will be required.")
    fun changeEmail(
        @RequestBody @Valid request: ChangeEmailRequest,
        twoFactorPrincipal: TwoFactorPrincipal,
        httpRequest: HttpServletRequest
    ): UpdateResult<String> {
        val userId = twoFactorPrincipal.userId.toLong()
        val user = userService.getUserById(userId) ?: throw UserNotFoundException()
        checkPasswordOrOneTimeCode(httpRequest, user, request.password)


        return request.newEmail.let {
            val verify = it.verify ?: throw InvalidOneTimeCodeException()

            oneTimeCodeService.verifyCode(
                verify.code,
                verify.stamp,
                channel = OneTimeCodeTarget.Channel.Email,
                it.email,
                throwIfInvalid = true)

            val success = userService.changeEmail(userId, it.email, true)
            UpdateResult(it.email.maskPhone(), success)
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @Operation(description = "If the old password is empty, `TOTP` verification will be required.")
    fun changePassword(
        @RequestBody @Valid request: ChangePasswordRequest,
        httpRequest: HttpServletRequest,
    ): SimpleValue<Boolean> {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        val user = userService.getUserById(userId) ?: throw UserNotFoundException()

        checkPasswordOrOneTimeCode(httpRequest, user, request.oldPassword)

        val result = userService.resetPassword(userId, request.newPassword)
        return SimpleValue(result)
    }
}