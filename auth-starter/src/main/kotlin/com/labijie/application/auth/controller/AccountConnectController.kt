package com.labijie.application.auth.controller

import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.auth.toUserDetails
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.model.SignInInfo
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.SimpleValue
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.verify
import com.labijie.infra.oauth2.AccessToken
import com.labijie.infra.oauth2.OAuth2ServerUtils.toAccessToken
import com.labijie.infra.oauth2.StandardOidcUser
import com.labijie.infra.oauth2.TwoFactorPrincipal
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import io.swagger.v3.oas.annotations.Operation
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotBlank
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
@RestController
@RequestMapping("/account/connect")
@Validated
class AccountConnectController(
    private val transactionTemplate: TransactionTemplate,
    private val userService: IUserService,
    private val oauth2UserService: IOAuth2ClientUserService,
    private val oneTimeCodeService: IOneTimeCodeService,
    private val signInHelper: TwoFactorSignInHelper,
) {

    private fun bind(
        oauth2User: StandardOidcUser,
        signInInfo: SignInInfo,
        httpRequest: HttpServletRequest
    ): UserAndRoles {
        val password = signInInfo.password
        return this.transactionTemplate.execute {
            val u = userService.getUser(signInInfo.user) ?: throw UserNotFoundException(signInInfo.user)
            if (!password.isNullOrBlank()) {
                userService.validatePassword(u, password, throwIfInvalid = true)
            } else {
                oneTimeCodeService.verify(httpRequest, u, throwInfInvalid = true)
            }
            oauth2UserService.addUserLoginToUser(u.id, oauth2User)
            val roles = userService.getUserRoles(u.id)
            UserAndRoles(u, roles)
        }!!
    }

    private fun bindMe(userId: Long, oauth2User: StandardOidcUser) {
        this.transactionTemplate.execute {
            val u = userService.getUserById(userId) ?: throw UserNotFoundException()

            oauth2UserService.addUserLoginToUser(u.id, oauth2User)
        }
    }


    @ClientRequired
    @PermitAll
    @PutMapping
    @Operation(description = "If the password is empty, `TOTP` verification will be required.")
    fun bindAccount(
        @RequestParam("user", required = true)
        @NotBlank
        user: String,

        @RequestParam("password", required = false)
        password: String? = null,

        httpRequest: HttpServletRequest,
        client: RegisteredClient,

        @ServerIdToken
        oidcUser: StandardOidcUser
    ): AccessToken {
        val signInInfo = SignInInfo().apply {
            this.user = user
            this.password = password
        }
        val loggedUser = bind(oidcUser, signInInfo, httpRequest)
        val auth = signInHelper.signIn(client, loggedUser.toUserDetails(), request = httpRequest)
        return auth.toAccessToken()
    }

    @PutMapping("/me")
    fun connectMe(
        @ServerIdToken oidcUser: StandardOidcUser,

        twoFactorPrincipal: TwoFactorPrincipal
    ): SimpleValue<Boolean> {
        val userId = twoFactorPrincipal.userId.toLong()
        bindMe(userId, oidcUser)
        return SimpleValue(true)
    }

    @DeleteMapping("/me/{provider}")
    fun disconnectMe(
        @PathVariable("provider") @NotBlank provider: String,
        twoFactorPrincipal: TwoFactorPrincipal
    ): SimpleValue<Boolean> {
        val userId = twoFactorPrincipal.userId.toLong()
        val login = oauth2UserService.deleteUserLoginToUser(userId, provider)
        return SimpleValue(login != null)
    }

}