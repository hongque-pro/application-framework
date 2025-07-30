/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.service

import com.labijie.application.identity.model.UserAndRoles
import com.labijie.infra.oauth2.client.StandardOidcUser


interface IOAuth2ClientUserService {
    fun getUserByOAuth2User(provider: String, oauth2UserId: String, includeRoles: Boolean = false): UserAndRoles?

    fun addOAuth2UserLoginToUser(userId: Long, oauth2UserToken: StandardOidcUser)

}


fun IOAuth2ClientUserService.getUserByOAuth2User(oauth2UserToken: StandardOidcUser): UserAndRoles? {
    return getUserByOAuth2User(oauth2UserToken.provider, oauth2UserToken.userId)
}