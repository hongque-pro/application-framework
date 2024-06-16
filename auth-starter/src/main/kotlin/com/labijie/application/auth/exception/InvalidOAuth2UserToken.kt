/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedStatusException
import com.labijie.application.auth.AuthErrors
import org.springframework.http.HttpStatus


class InvalidOAuth2UserToken(message: String? = null, cause: Throwable? = null,) :
    ErrorCodedStatusException(AuthErrors.INVALID_OAUTH2_USER_TOKEN, message,cause, HttpStatus.FORBIDDEN) {
}