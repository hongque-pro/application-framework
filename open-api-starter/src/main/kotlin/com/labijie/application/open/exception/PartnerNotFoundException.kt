package com.labijie.application.open.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.open.OpenApiErrors

class PartnerNotFoundException(message: String? = null) : ErrorCodedException(
    OpenApiErrors.PartnerNotFound,
    message ?: "Partner was not found."
)