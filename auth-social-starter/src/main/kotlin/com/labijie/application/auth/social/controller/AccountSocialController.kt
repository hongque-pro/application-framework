package com.labijie.application.auth.social.controller

import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.social.OAuth2SocialConstants
import com.labijie.application.auth.social.exception.BadSocialCredentialsException
import com.labijie.application.auth.social.exception.SocialUserLockedException
import com.labijie.application.auth.social.model.SocialLoginInfo
import com.labijie.application.auth.toPrincipal
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.identity.isEnabled
import com.labijie.application.identity.model.SocialRegisterInfo
import com.labijie.application.identity.model.SocialUserAndRoles
import com.labijie.application.identity.service.ISocialUserService
import com.labijie.application.model.OneTimeCode
import com.labijie.application.model.OneTimeCodeVerificationRequest
import com.labijie.application.verify
import com.labijie.application.web.annotation.OneTimeCodeVerify
import com.labijie.infra.oauth2.AccessToken
import com.labijie.infra.oauth2.OAuth2ServerUtils.toAccessToken
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import jakarta.validation.Valid
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
@Validated
@RestController
@RequestMapping("/oauth/social")
class AccountSocialController(
    private val oneTimeCodeService: IOneTimeCodeService,
    private val userService: ISocialUserService,
    private val signInHelper: TwoFactorSignInHelper,
    private val authProperties: AuthProperties,
) {

    @OneTimeCodeVerify
    @PostMapping("/register")
    @ClientRequired
    fun register(
        @RequestBody @Valid info: SocialRegisterInfo,
        clientDetails: RegisteredClient,
        request: OneTimeCodeVerificationRequest
    ): AccessToken {

        oneTimeCodeService.verifyCode(request.code, request.stamp, true)
        val userRoles = userService.registerSocialUser(info, authProperties.registerBy)
        return signInUser(userRoles, clientDetails)
    }

    @PostMapping("/login")
    @ClientRequired
    fun login(
        @RequestBody @Valid info: SocialLoginInfo,
        clientDetails: RegisteredClient
    ): AccessToken {
        val userRoles =
            userService.getSocialUser(info.provider, info.code) ?: throw BadSocialCredentialsException(info.provider)
        return signInUser(userRoles, clientDetails)
    }

    private fun signInUser(
        userRoles: SocialUserAndRoles,
        clientDetails: RegisteredClient
    ): AccessToken {
        val u = userRoles.user

        //账号是否锁定
        val userLocked = !u.isEnabled()
        if (userLocked) throw SocialUserLockedException(userRoles.loginProvider)

        val principal = userRoles.toPrincipal {
            mapOf(
                OAuth2SocialConstants.LoginProviderFieldName to this.loginProvider,
                OAuth2SocialConstants.LoginProviderKeyFieldName to this.loginProviderKey
            )
        }

        return signInHelper.signIn(
            clientDetails,
            principal,
            false
        ).toAccessToken()
    }
}