package com.labijie.application.auth.controller

import com.labijie.application.auth.model.*
import com.labijie.application.auth.toHttpResponse
import com.labijie.application.auth.toPrincipal
import com.labijie.application.component.IMessageService
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.crypto.DesUtils
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.isNullEmail
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.service.IUserService
import com.labijie.application.maskEmail
import com.labijie.application.maskPhone
import com.labijie.application.model.BindingStatus
import com.labijie.application.model.SmsAssociated
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.UpdateResult
import com.labijie.application.verifySmsCode
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.oauth2.TwoFactorPrincipal
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import com.labijie.infra.utils.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Duration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */

@RestController
@RequestMapping("/account")
class AccountController @Autowired constructor(
    private val userService: IUserService,
    private val messageService: IMessageService,
    private val signInHelper: TwoFactorSignInHelper,
    private val applicationProperties: ApplicationCoreProperties
) {

    init {
        logger.info("AccountController loaded")
    }

    @ClientRequired
    @RequestMapping("/register", method = [RequestMethod.POST])
    fun register(@RequestBody @Validated info: RegisterInfo, client: RegisteredClient): Map<String, Any> {
        val userAndRoles = userService.registerUser(info)

        return signInHelper.signIn(
            client,
            userAndRoles.toPrincipal(),
            false
        ).toHttpResponse()
    }

    @GetMapping("/current")
    fun currentUser(): TwoFactorPrincipal {
        return OAuth2Utils.currentTwoFactorPrincipal()
    }

    private fun Boolean?.toBindingStatus(): BindingStatus {
        return if (this == true) BindingStatus.Confirmed else BindingStatus.UnConfirmed
    }

    /**
     * 找回密码第一步，验证身份
     */
    @PostMapping("/verify")
    fun verify(@RequestParam("u", required = false) usr: String): UserVerifyResult {
        val u = if (usr.isNotBlank()) userService.getUser(usr) else null
        val user = u ?: throw UserNotFoundException(usr)
        return UserVerifyResult(user.id, securityView(user))
    }

    /**
     * 找回密码（需要验证短信验证码）
     */
    @PostMapping("/forgot-password")
    fun resetPassword(@RequestBody @Validated request: SetPasswordRequest): SimpleValue<Boolean> {
        val u = userService.getUserById(request.userId) ?: throw  UserNotFoundException()

        messageService.verifySmsCode(request, true)
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
            this.mobile = user.phoneNumber.maskPhone()
            this.mobileStatus = user.phoneNumberConfirmed.toBindingStatus()
        }
    }

    /**
     * 变更手机号，验证身份（需要使用旧的手机号验证短信验证码）
     */
    @PostMapping("/change-phone-verify")
    fun getChangePhoneToken(@RequestBody @Validated request: SmsAssociated): SimpleValue<String> {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId
        messageService.verifySmsCode(request, true)
        val u = userService.getUserById(userId.toLong()) ?: throw UserNotFoundException()
        val token = DesUtils.generateToken(userId, Duration.ofMinutes(10), u.securityStamp)
        return SimpleValue(token)
    }

    /**
     * 变更新手机号，需要上一步的 token
     */
    @PostMapping("/change-phone")
    fun changePhoneNumber(@RequestBody @Validated request: ChangePhoneRequest): UpdateResult<String> {
        messageService.verifySmsCode(request, true)
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        val u = userService.getUserById(userId) ?: throw UserNotFoundException()
        DesUtils.verifyToken(request.token, userId.toString(), u.securityStamp, throwIfInvalid = true)
        val success = userService.changePhone(userId, request.phoneNumber, true)
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