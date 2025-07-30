/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.oauth2

import com.labijie.infra.oauth2.client.StandardOidcUser


data class OAuth2UserTokenAndValue(
    val token: StandardOidcUser,
    val tokenValue: String
)