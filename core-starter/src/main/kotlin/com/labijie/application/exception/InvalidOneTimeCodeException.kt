package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedStatusException
import org.springframework.http.HttpStatus

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
class InvalidOneTimeCodeException(reason: String? = null, message: String? = null, cause: Throwable? = null) :
    ErrorCodedStatusException(ApplicationErrors.InvalidOneTimeCode, message, cause = cause, status = statusOnFailure) {

    companion object {

        const val REASON_MISS_REQUEST_PARAM = "miss_code_or_stamp"
        const val REASON_INVALID_CONTACT = "invalid_contact"
        const val REASON_INVALID_CHANNEL = "invalid_channel"

        val statusOnFailure = HttpStatus.FORBIDDEN
    }

    init {
        reason?.let {
            args.putIfAbsent("reason", it)
        }
    }
}