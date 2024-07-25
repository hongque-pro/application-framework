/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException


class InvalidDisplayNameException(message:String? = null, val inputDisplayName: String? = null)
    : ErrorCodedException(ApplicationErrors.InvalidDisplayName, message, args = inputDisplayName?.let { mapOf("input" to inputDisplayName) }) {}