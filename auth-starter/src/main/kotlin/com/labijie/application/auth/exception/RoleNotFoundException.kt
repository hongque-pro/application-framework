package com.labijie.application.auth.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */
class RoleNotFoundException(identity: String) : ErrorCodedException(
    AuthErrors.ROLE_NOT_FOUND,
    "Role with identity '$identity' was not found"
) {

    constructor(roleId: Long) : this(roleId.toString())
}