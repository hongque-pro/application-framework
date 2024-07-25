package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 *
 * @author lishiwen
 * @date 19-10-29
 * @since JDK1.8
 */
class BadRequestException(message:String? = null)
    : ErrorCodedException(ApplicationErrors.BadRequestParameter, message)
