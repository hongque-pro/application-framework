/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.component

import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.identity.component.IUserRegistrationIntegration
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.infra.oauth2.service.IOAuth2ServerOidcTokenService


class OAuth2UserRegistrationIntegration(
    private val serverTokenService: IOAuth2ServerOidcTokenService,
    private val oauth2ClientUserService: IOAuth2ClientUserService
) : IUserRegistrationIntegration {
    override fun onUserRegisteredInTransaction(user: UserAndRoles, addition: Map<String, String>) {
        val tokenValue = addition[OAuth2UserTokenArgumentResolver.ID_TOKEN_KEY]
        if (!tokenValue.isNullOrBlank()) {
            val oauth2User = serverTokenService.decode(tokenValue)
            oauth2ClientUserService.addUserLoginToUser(user.user.id, oauth2User)
        }
    }
}