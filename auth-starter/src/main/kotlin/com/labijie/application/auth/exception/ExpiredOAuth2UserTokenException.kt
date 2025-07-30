package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedStatusException
import com.labijie.application.auth.AuthErrors
import org.springframework.http.HttpStatus

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class ExpiredOAuth2UserTokenException(message: String? = null, cause: Throwable? = null,) :
    ErrorCodedStatusException(AuthErrors.EXPIRED_OAUTH2_USER_TOKEN, message,cause, HttpStatus.FORBIDDEN) {
}