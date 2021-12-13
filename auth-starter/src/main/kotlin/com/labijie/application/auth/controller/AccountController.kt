package com.labijie.application.auth.controller

import com.labijie.application.auth.model.*
import com.labijie.application.auth.toPrincipal
import com.labijie.application.component.IMessageSender
import com.labijie.application.crypto.DesUtils
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.isNullEmail
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.service.IUserService
import com.labijie.application.maskEmail
import com.labijie.application.maskPhone
import com.labijie.application.model.BindingStatus
import com.labijie.application.model.CaptchaValidationRequest
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.UpdateResult
import com.labijie.application.verifyCaptcha
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.oauth2.TwoFactorPrincipal
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import com.labijie.infra.utils.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Duration
import javax.validation.Valid
import com.labijie.application.identity.data.UserRecord as User

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */

@RestController
@RequestMapping("/account")
@Validated
class AccountController @Autowired constructor(
        private val userService: IUserService,
        private val messageSender: IMessageSender,
        private val signInHelper: TwoFactorSignInHelper
) {

    init {
        logger.info("AccountController loaded")
    }

    @ClientRequired
    @RequestMapping("/register", method = [RequestMethod.POST])
    fun register(@Valid @RequestBody info: RegisterInfo, client: RegisteredClient): OAuth2AccessTokenAuthenticationToken {
        val userAndRoles = userService.registerUser(info)

        return signInHelper.signIn(
            client,
            userAndRoles.toPrincipal(),
            false
        )
    }

    @GetMapping("/current")
    fun currentUser(): TwoFactorPrincipal {
        return OAuth2Utils.currentTwoFactorPrincipal()
    }

    private fun Boolean?.toBindingStatus(): BindingStatus {
        return if(this == true) BindingStatus.Confirmed else BindingStatus.UnConfirmed
    }

    /**
     * 找回密码第一步，验证身份
     */
    @PostMapping("/verify")
    fun verify(@RequestParam("u", required = false) usr:String): UserVerifyResult {
        val u = if(usr.isNotBlank()) userService.getUser(usr) else null
        val user = u ?: throw UserNotFoundException(usr)
        return UserVerifyResult(user.id ?: 0, securityView(user))
    }

    /**
     * 找回密码（需要验证短信验证码）
     */
    @PostMapping("/set-password")
    fun setPassword(@RequestBody request: SetPasswordRequest){
         messageSender.verifySmsCaptcha(request.captcha, request.stamp, request.userId.toString(), true)
        userService.resetPassword(request.userId, request.password)
    }

    @GetMapping("/security")
    fun securityInfo(): SecurityView {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        val user = userService.getUserById(userId)
        if(user != null) {
            return securityView(user)
        }
        return SecurityView()
    }

    private fun securityView(user: User): SecurityView {
        return SecurityView().apply {
            this.email = if (user.isNullEmail) "" else user.email.orEmpty().maskEmail()
            this.emialStatus = if (user.isNullEmail) BindingStatus.Unbound else user.emailConfirmed.toBindingStatus()
            this.mobile = user.phoneNumber.orEmpty().maskPhone()
            this.mobileStatus = user.phoneNumberConfirmed.toBindingStatus()
        }
    }

    /**
     * 变更手机号，验证身份（需要使用旧的手机号验证短信验证码）
     */
    @PostMapping("/change-phone-verify")
    fun  getChangePhoneToken(@RequestBody request: CaptchaValidationRequest): SimpleValue<String> {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId
        messageSender.verifySmsCaptcha(request.captcha, request.stamp,  userId, true)
        val token = DesUtils.generateToken(userId, Duration.ofMinutes(10))
        return SimpleValue(token)
    }

    /**
     * 变更新手机号，需要上一步的 token
     */
    @PostMapping("/change-phone")
    fun changePhoneNumber(@RequestBody request: ChangePhoneRequest): UpdateResult<String> {
        messageSender.verifyCaptcha(request, request.phoneNumber, true)
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        DesUtils.verifyToken(request.token, userId.toString(),  throwIfInvalid = true)
        val success = userService.changePhone(userId, request.phoneNumber, true)
        return UpdateResult(request.phoneNumber.maskPhone(), success)
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    fun changePassword(@RequestBody request:ChangePasswordRequest): SimpleValue<Boolean> {
        val userId = OAuth2Utils.currentTwoFactorPrincipal().userId.toLong()
        val result = userService.changePassword(userId, request.oldPassword, request.newPassword)
        return SimpleValue(result)
    }
}