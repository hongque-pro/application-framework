/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedStatusException
import com.labijie.application.auth.AuthErrors
import org.springframework.http.HttpStatus


class InvalidOAuth2ClientRegistrationException(message: String? = null, cause: Throwable? = null,) :
    ErrorCodedStatusException(AuthErrors.INVALID_OAUTH2_CLIENT_REGISTRATION, message,cause, HttpStatus.FORBIDDEN) {
}