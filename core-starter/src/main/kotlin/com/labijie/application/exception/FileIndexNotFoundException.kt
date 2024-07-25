package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * @author Anders Xiao
 * @date 2023-12-07
 */
class FileIndexNotFoundException(filePath: String? = null, message: String? = null) :
    ErrorCodedException(
        ApplicationErrors.FileIndexNotFound,
        message,
        args = filePath?.let { mapOf("filePath" to filePath) }
    )