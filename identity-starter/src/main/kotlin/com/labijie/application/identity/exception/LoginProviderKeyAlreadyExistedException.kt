package com.labijie.application.identity.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.identity.IdentityErrors

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/1/25
 * @Description:
 */
class LoginProviderKeyAlreadyExistedException(loginProvider: String, message: String? = null): ErrorCodedException(
    IdentityErrors.LOGIN_PROVIDER_KEY_ALREADY_EXISTED,
    message ?: "Current user has an existed login provider key ( provider: $loginProvider)."
) {
    init {
        args.putIfAbsent("provider", loginProvider)
    }
}