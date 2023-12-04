package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 *
 * @author lishiwen
 * @date 19-12-18
 * @since JDK1.8
 */
class PermissionDeniedException(message:String? = null)
    : ErrorCodedException(ApplicationErrors.PermissionDenied, message?:"Permission Denied")
