package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedStatusException
import org.springframework.http.HttpStatus

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
abstract class OAuth2UserTokenException(error: String, message: String? = null, cause: Throwable? = null)
: ErrorCodedStatusException(error, message, cause, HttpStatus.FORBIDDEN) {

}