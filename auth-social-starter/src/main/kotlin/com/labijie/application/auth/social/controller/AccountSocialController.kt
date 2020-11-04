package com.labijie.application.auth.social.controller

import com.labijie.application.auth.social.OAuth2SocialConstants
import com.labijie.application.auth.social.exception.BadSocialCredentialsException
import com.labijie.application.auth.social.exception.SocialUserLockedException
import com.labijie.application.auth.social.model.SocialLoginInfo
import com.labijie.application.auth.social.model.SocialRegisterInfo
import com.labijie.application.auth.social.model.SocialUserAndRoles
import com.labijie.application.auth.social.service.ISocialUserService
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
@Validated
@RestController
@RequestMapping("/oauth/social")
class AccountSocialController(
    private val userService: ISocialUserService,
    private val signInHelper: TwoFactorSignInHelper
) {

    @PostMapping("/register")
    @ClientRequired
    fun register(@RequestBody @Valid info: SocialRegisterInfo, clientDetails: ClientDetails): OAuth2AccessToken {
        val userRoles = userService.registerSocialUser(info)
        return signInUser(userRoles, clientDetails)
    }

    @PostMapping("/login")
    @ClientRequired
    fun login(@RequestBody @Valid info: SocialLoginInfo, clientDetails: ClientDetails): OAuth2AccessToken {
        val userRoles =
            userService.signInSocialUser(info.provider, info.code) ?: throw BadSocialCredentialsException(info.provider)
        return signInUser(userRoles, clientDetails)
    }

    private fun signInUser(
        userRoles: SocialUserAndRoles,
        clientDetails: ClientDetails
    ): OAuth2AccessToken {
        val u = userRoles.user
        val roles = userRoles.roles.map {
            it.name.orEmpty()
        }

        //账号是否锁定
        val userLocked = (u.lockoutEnabled ?: false && (u.lockoutEnd ?: 0) > System.currentTimeMillis())
        if (userLocked) throw SocialUserLockedException(userRoles.loginProvider)

        return signInHelper.signIn(
            clientDetails.clientId,
            u.id!!.toString(),
            u.userName!!,
            authorities = roles,
            attachedFields = if (userRoles.loginProvider.isNotBlank()) {
                mapOf(
                    OAuth2SocialConstants.LoginProviderFieldName to userRoles.loginProvider,
                    OAuth2SocialConstants.LoginProviderKeyFieldName to userRoles.loginProviderKey
                )
            } else mapOf()
        )
    }
}