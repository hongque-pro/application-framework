/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.service

import com.labijie.application.auth.oauth2.OAuth2UserToken
import com.labijie.application.identity.model.UserAndRoles


interface IOAuth2ClientUserService {
    fun getUserByOAuth2User(clientRegistrationId: String, oauth2UserId: String, includeRoles: Boolean = false): UserAndRoles?

    fun addOAuth2UserLoginToUser(userId: Long, oauth2UserToken: OAuth2UserToken)
}


fun IOAuth2ClientUserService.getUserByOAuth2User(oauth2UserToken: OAuth2UserToken): UserAndRoles? {
    return getUserByOAuth2User(oauth2UserToken.clientRegistrationId, oauth2UserToken.id)
}