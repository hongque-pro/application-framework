/**
 * @author Anders Xiao
 * @date 2024-07-25
 */
package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException


class TemporaryFileTimoutException(message: String? = null) : ErrorCodedException(ApplicationErrors.TemporaryFileTimout, message)