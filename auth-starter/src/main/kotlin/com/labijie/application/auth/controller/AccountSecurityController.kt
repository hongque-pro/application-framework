package com.labijie.application.auth.controller

import com.labijie.application.auth.model.*
import com.labijie.application.component.IVerificationCodeService
import com.labijie.application.component.verify
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.isNullEmail
import com.labijie.application.identity.isNullPhoneNumber
import com.labijie.application.identity.service.IUserService
import com.labijie.application.maskEmail
import com.labijie.application.maskPhone
import com.labijie.application.model.BindingStatus
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.UpdateResult
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.utils.logger
import jakarta.annotation.security.PermitAll
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */

@RestController
@RequestMapping("/account")
class AccountSecurityController @Autowired constructor(
    private val userService: IUserService,
    private val verificationCodeService: IVerificationCodeService,
) {

    init {
        logger.info("AccountController loaded")
    }


    private fun Boolean?.toBindingStatus(): BindingStatus {
        return if (this == true) BindingStatus.Confirmed else BindingStatus.UnConfirmed
    }

    /**
     * 找回密码第一步，验证身份
     */
    @PermitAll
    @PostMapping("/verify-user")
    fun verify(@RequestParam("u", required = false) @NotBlank usr: String): UserVerifyResult {
        val u = if (usr.isNotBlank()) userService.getUser(usr) else null
        val user = u ?: throw UserNotFoundException(usr)
        return UserVerifyResult(user.id, securityView(user))
    }

    /**
     * 找回密码（需要验证短信验证码）
     */
    @PostMapping("/forgot-password")
    fun resetPassword(@RequestBody @Validated request: SetPasswordRequest): SimpleValue<Boolean> {
        userService.getUserById(request.userId) ?: throw  UserNotFoundException()

        verificationCodeService.verify(request, true)
        val succeed = userService.resetPassword(request.userId, request.password)
        return SimpleValue(succeed)
    }

    @GetMapping("/security")
    fun securityInfo(): SecurityView {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        val view = userService.getUserById(userId)?.let {
            securityView(it)
        }
        return view ?: SecurityView()
    }

    private fun securityView(user: User): SecurityView {
        return SecurityView().apply {
            this.email = if (user.isNullEmail) "" else user.email.maskEmail()
            this.emailStatus = if (user.isNullEmail) BindingStatus.Unbound else user.emailConfirmed.toBindingStatus()

            this.phoneNumber = user.phoneNumber.maskPhone()
            this.dialingCode = user.phoneCountryCode

            this.phoneStatus = if(user.isNullPhoneNumber)  BindingStatus.Unbound else user.phoneNumberConfirmed.toBindingStatus()
        }
    }

    /**
     * 变更新手机号
     */
    @PostMapping("/change-phone")
    fun changePhoneNumber(@RequestBody @Validated request: ChangePhoneRequest): UpdateResult<String> {
        verificationCodeService.verify(request, true)
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        userService.getUserById(userId) ?: throw UserNotFoundException()

        val success = userService.changePhone(userId, request.dialingCode, request.phoneNumber, true)
        return UpdateResult(request.phoneNumber.maskPhone(), success)
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    fun changePassword(@RequestBody @Validated request: ChangePasswordRequest): SimpleValue<Boolean> {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        val result = userService.changePassword(userId, request.oldPassword, request.newPassword)
        return SimpleValue(result)
    }
}