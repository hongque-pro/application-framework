package com.labijie.application.auth.component

import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.auth.toUserDetails
import com.labijie.infra.oauth2.client.IOidcLoginHandler
import com.labijie.infra.oauth2.client.StandardOidcUser
import com.labijie.infra.oauth2.mvc.OidcLoginRequest
import com.labijie.infra.oauth2.mvc.OidcLoginResponse

/**
 * @author Anders Xiao
 * @date 2025/7/26
 */
class DefaultOidcLoginHandler(
    private val oauth2ClientUserService: IOAuth2ClientUserService) : IOidcLoginHandler {

    override fun handle(user: StandardOidcUser, request: OidcLoginRequest): OidcLoginResponse {

        val u = oauth2ClientUserService.getUserByOAuth2User(user.provider, user.userId, true)
        if(u == null) {
            return OidcLoginResponse.accountNotRegistered()
        }
        return OidcLoginResponse.success(u.toUserDetails())
    }
}