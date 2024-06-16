/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.service

import com.labijie.application.auth.oauth2.OAuth2UserToken


interface IOAuth2UserTokenCodec {
    fun encode(token: OAuth2UserToken): String

    fun decode(tokenValue: String, check: Boolean = false): OAuth2UserToken
}