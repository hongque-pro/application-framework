/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.component

import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.auth.service.IOAuth2UserTokenCodec
import com.labijie.application.identity.component.IUserRegistrationIntegration
import com.labijie.application.identity.model.UserAndRoles


class OAuth2UserRegistrationIntegration(
    private val oauth2UserTokenCodec: IOAuth2UserTokenCodec,
    private val oauth2ClientUserService: IOAuth2ClientUserService
) : IUserRegistrationIntegration {
    override fun onUserRegisteredInTransaction(user: UserAndRoles, addition: Map<String, String>) {
        val tokenValue = addition[OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME]
        if (!tokenValue.isNullOrBlank()) {
            val oauth2UserToken = oauth2UserTokenCodec.decode(tokenValue, false)
            oauth2ClientUserService.addOAuth2UserLoginToUser(user.user.id, oauth2UserToken)
        }
    }
}