package com.labijie.application.auth.social.exception

import com.labijie.application.auth.AuthErrors
import org.springframework.http.HttpStatus

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class BadSocialCredentialsException(provider: String, message: String? = null) : AuthSocialException(
    provider,
    AuthErrors.INVALID_GRANT,
    message ?: "Bad social account."
) {
    override val status: HttpStatus = HttpStatus.UNAUTHORIZED
}