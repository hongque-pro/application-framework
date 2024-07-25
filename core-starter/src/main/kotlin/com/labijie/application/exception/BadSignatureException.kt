package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedStatusException
import org.springframework.http.HttpStatus

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class BadSignatureException(message: String? = null, var platform: String? = null) : ErrorCodedStatusException(
    ApplicationErrors.InvalidSignature,
    message,
    args = platform?.let { mapOf("platform" to it) }
) {
    override val status: HttpStatus = HttpStatus.PRECONDITION_FAILED
}