/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException


class InvalidUsernameException(message:String? = null, val inputUsername: String? = null)
    : ErrorCodedException(ApplicationErrors.InvalidUsername, message, args = inputUsername?.let { mapOf("input" to inputUsername) }) {}