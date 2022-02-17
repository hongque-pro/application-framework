package com.labijie.application.open.exception

import com.labijie.application.ErrorCodedException
import com.labijie.application.open.OpenApiErrors

class AppNotFoundException(message: String? = null) : ErrorCodedException(
    OpenApiErrors.AppNotFound,
    message ?: "App was not found."
)