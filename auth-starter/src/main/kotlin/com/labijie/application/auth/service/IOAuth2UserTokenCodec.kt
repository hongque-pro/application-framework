/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.service

import com.labijie.infra.oauth2.client.StandardOidcUser
import java.time.Duration


interface IOAuth2UserTokenCodec {
    fun encode(token: StandardOidcUser, expiration: Duration): String
    fun decode(tokenValue: String, check: Boolean = false): StandardOidcUser
}