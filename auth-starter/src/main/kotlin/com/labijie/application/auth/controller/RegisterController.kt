/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.auth.controller

import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.application.auth.attachIdToken
import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.toPrincipal
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.AccessToken
import com.labijie.infra.oauth2.OAuth2ServerUtils.toAccessToken
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import io.swagger.v3.oas.annotations.Operation
import jakarta.annotation.security.PermitAll
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/account/register")
@PermitAll
class RegisterController(
    private val userService: IUserService,
    private val signInHelper: TwoFactorSignInHelper,
    private val authProperties: AuthProperties,
) {
    @ClientRequired
    @PostMapping
    @Operation(description = "If the password is empty, `TOTP` verification will be required.")
    fun register(
        @ServerIdToken(required = false) idToken: String? = null,
        @RequestBody @Validated info: RegisterInfo, client: RegisteredClient): AccessToken {
        if(!idToken.isNullOrBlank()) {
            info.attachIdToken(idToken)
        }
        val userAndRoles = userService.registerUser(info, authProperties.registerBy)

        return signInHelper.signIn(
            client,
            userAndRoles.toPrincipal(),
            false
        ).toAccessToken()
    }
}