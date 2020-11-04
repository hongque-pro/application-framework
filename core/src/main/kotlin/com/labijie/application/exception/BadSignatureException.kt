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
    message ?: "Invalid signature for request content"
) {
    override val status: HttpStatus = HttpStatus.PRECONDITION_FAILED


    override val message: String?
        get() = if (!platform.isNullOrBlank()) "${super.message} (platform: $platform)" else super.message
}