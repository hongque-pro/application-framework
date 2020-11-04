package com.labijie.application.open.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.open.OpenApiErrors

class UnsupportedSignAlgorithmException(message:String? = null) : ErrorCodedException(
    OpenApiErrors.UnsupportedSignAlgorithm,
    message ?: "Unsupported signature algorithm."
)