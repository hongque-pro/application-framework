/**
 * @author Anders Xiao
 * @date 2024-06-10
 */
package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException


class StrongPasswordViolationException(message: String? = null, val inputPassword: String? = null): ErrorCodedException(ApplicationErrors.StrongPasswordConstraintViolation, message ?: "Object not found in storage.")