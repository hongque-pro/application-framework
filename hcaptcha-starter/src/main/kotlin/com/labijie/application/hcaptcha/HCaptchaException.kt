package com.labijie.application.hcaptcha

import com.labijie.application.ErrorCodedException

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
class HCaptchaException(error: String, errorCodes: List<String>? = null,  message: String? = null) : ErrorCodedException(error, message ?: "Hcaptcha process verification failed") {

    init {
        errorCodes?.let {
            if(errorCodes.isNotEmpty()) {
                args["hcaptcha_errors"] = errorCodes.joinToString(", ")
            }
        }
    }
}