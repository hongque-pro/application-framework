package com.labijie.application.open.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.open.OpenApiErrors

class InvalidOpenApiSignatureException(message: String? = null) : ErrorCodedException(
    OpenApiErrors.InvalidSignature,
    message ?: "Bad signature"
)