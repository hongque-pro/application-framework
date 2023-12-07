package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException
import com.labijie.infra.utils.ifNullOrBlank

/**
 * @author Anders Xiao
 * @date 2023-12-07
 */
class FileIndexNotFoundException(filePath: String? = null, message: String? = null) :
    ErrorCodedException(
        ApplicationErrors.FileIndexNotFound,
        message.ifNullOrBlank {
            filePath?.let {
                "File index '${filePath}' not found."
            } ?: "File index not found."
        }
    )