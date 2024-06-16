/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.oauth2


data class OAuth2UserTokenAndValue(
    val token: OAuth2UserToken,
    val tokenValue: String
)