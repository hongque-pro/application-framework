package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
class PhoneAlreadyExistedException(message:String? = null): ErrorCodedException(
    AuthErrors.PHONE_ALREADY_EXISTED,
    message ?: "Phone number has been used."
)