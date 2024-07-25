package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-14
 */
class DataNotFoundException(message: String? = null) :
    ErrorCodedException(
        ApplicationErrors.DataMissed,
        message
    )