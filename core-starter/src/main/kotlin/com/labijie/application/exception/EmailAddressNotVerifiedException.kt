package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class EmailAddressNotVerifiedException(message:String? = null)
    : ErrorCodedException(ApplicationErrors.EmailAddressNotVerified, message)