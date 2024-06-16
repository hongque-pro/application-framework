/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException


class InvalidEmailException(message:String? = null, val inputEmail: String?)
    : ErrorCodedException(ApplicationErrors.InvalidEmailAddress, message?: "Bad email address") {}