package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
class FileIndexAlreadyExistedException(filePath: String? = null, message: String? = null) :
    ErrorCodedException(
        ApplicationErrors.FileIndexAlreadyExisted,
        message,
        args = filePath?.let { mapOf("filePath" to filePath) }
    )