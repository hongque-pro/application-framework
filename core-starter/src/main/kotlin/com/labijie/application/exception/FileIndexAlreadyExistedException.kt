package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException
import com.labijie.infra.utils.ifNullOrBlank

/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
class FileIndexAlreadyExistedException(filePath: String? = null, message: String? = null) :
    ErrorCodedException(
        ApplicationErrors.FileIndexAlreadyExisted,
        message.ifNullOrBlank {
            filePath?.let {
                "File index '${filePath}' already existed."
            } ?: "File index already existed."
        }
    )