package com.labijie.application.identity.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.identity.IdentityErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-08
 */
class RoleNotFoundException(identity: String) : ErrorCodedException(
    IdentityErrors.ROLE_NOT_FOUND,
    "Role with identity '$identity' was not found"
) {

    constructor(roleId: Long) : this(roleId.toString())
}