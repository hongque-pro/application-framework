package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedStatusException
import org.springframework.http.HttpStatus

/**
 *
 * @author lishiwen
 * @date 19-12-18
 * @since JDK1.8
 */
class PermissionDeniedException(message:String? = null)
    : ErrorCodedStatusException(ApplicationErrors.PermissionDenied, message, status = HttpStatus.FORBIDDEN)
