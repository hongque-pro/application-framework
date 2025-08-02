/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.auth.controller

import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.application.auth.attachIdToken
import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.toPrincipal
import com.labijie.application.exception.InvalidOneTimeCodeException
import com.labijie.application.getOneTimeCodeInRequest
import com.labijie.application.identity.model.RegisterBy
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.service.IUserService
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.web.InvalidRequestArgumentsException
import com.labijie.infra.oauth2.AccessToken
import com.labijie.infra.oauth2.OAuth2ServerUtils.toAccessToken
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
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
    private val oneTimeCodeService: IOneTimeCodeService,
) {
    @ClientRequired
    @PostMapping
    fun register(
        @ServerIdToken(required = false) idToken: String? = null,
        @RequestBody @Validated info: RegisterInfo, client: RegisteredClient,
        httpRequest: HttpServletRequest): AccessToken {

        if(!idToken.isNullOrBlank()) {
            info.attachIdToken(idToken)
        }

        var registerBy: RegisterBy? = null
        if(authProperties.registerEndpoint.verifyEmailOrPhone) {
            if(!info.hasEmail() && !info.hasPhone()) {
                throw InvalidRequestArgumentsException("identifier", "Email or Phone is required")
            }

            val totpRequest = httpRequest.getOneTimeCodeInRequest() ?: throw InvalidOneTimeCodeException()
            val result = oneTimeCodeService.verifyCode(totpRequest.code, totpRequest.stamp, throwIfInvalid = true)
            val t = result.target ?: throw InvalidOneTimeCodeException()
            if(info.email == t.contact) {
                registerBy = RegisterBy.Email
            }
            if(info.fullPhoneNumber() == t.contact) {
                registerBy = RegisterBy.Email
            }
        }

        val userAndRoles = userService.registerUser(info, registerBy)

        return signInHelper.signIn(
            client,
            userAndRoles.toPrincipal(),
            false
        ).toAccessToken()
    }
}