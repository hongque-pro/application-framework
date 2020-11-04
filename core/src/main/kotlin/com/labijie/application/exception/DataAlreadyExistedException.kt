package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 *
 * @author lishiwen
 * @date 19-12-17
 * @since JDK1.8
 */
class DataAlreadyExistedException(message:String? = null) : ErrorCodedException(ApplicationErrors.DataExisted, message ?: "data already existed")
