/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.exception

import com.labijie.application.auth.AuthErrors


class InvalidOAuth2UserTokenException(message: String? = null, cause: Throwable? = null) :
    OAuth2UserTokenException(AuthErrors.INVALID_OAUTH2_USER_TOKEN, message,cause) {
}