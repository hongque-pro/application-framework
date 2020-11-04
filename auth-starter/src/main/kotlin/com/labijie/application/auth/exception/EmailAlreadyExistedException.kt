package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
class EmailAlreadyExistedException(message:String? = null): ErrorCodedException(
    AuthErrors.EMAIL_ALREADY_EXISTED,
    message ?: "Email has been used."
)