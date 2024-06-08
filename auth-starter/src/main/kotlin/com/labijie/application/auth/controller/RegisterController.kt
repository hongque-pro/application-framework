/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.auth.controller

import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.toHttpResponse
import com.labijie.application.auth.toPrincipal
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.TwoFactorSignInHelper
import com.labijie.infra.oauth2.filter.ClientRequired
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/account")
class RegisterController(
    private val userService: IUserService,
    private val signInHelper: TwoFactorSignInHelper,
    private val authProperties: AuthProperties,
) {
    @ClientRequired
    @RequestMapping("/register", method = [RequestMethod.POST])
    fun register(@RequestBody @Validated info: RegisterInfo, client: RegisteredClient): Map<String, Any> {
        val userAndRoles = userService.registerUser(info, authProperties.registerBy)

        return signInHelper.signIn(
            client,
            userAndRoles.toPrincipal(),
            false
        ).toHttpResponse()
    }
}