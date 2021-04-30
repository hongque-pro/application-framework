package com.labijie.application.identity.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.identity.IdentityErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
class PhoneAlreadyExistedException(message:String? = null): ErrorCodedException(
    IdentityErrors.PHONE_ALREADY_EXISTED,
    message ?: "Phone number has been used."
)