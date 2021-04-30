package com.labijie.application.identity.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.identity.IdentityErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
class EmailAlreadyExistedException(message:String? = null): ErrorCodedException(
    IdentityErrors.EMAIL_ALREADY_EXISTED,
    message ?: "Email has been used."
)