package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedStatusException
import org.springframework.http.HttpStatus

/**
 * @author Anders Xiao
 * @date 2025/8/3
 */
class RobotDetectedException(message:String? = null, args: Map<String, String>? = null, cause: Throwable? = null) :
    ErrorCodedStatusException(ApplicationErrors.RobotDetected, message, cause, statusOnFailure, args) {
    companion object {
        val statusOnFailure = HttpStatus.FORBIDDEN
    }
}
