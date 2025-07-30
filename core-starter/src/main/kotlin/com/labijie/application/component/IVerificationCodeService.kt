package com.labijie.application.component

import com.labijie.application.exception.InvalidVerificationCodeException
import com.labijie.application.model.VerificationAssociated

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
interface IVerificationCodeService {
    fun verifyCode(code: String, verificationToken: String, throwIfInvalid: Boolean = false): Boolean

    fun generateCode(verificationToken: String): String
}

fun IVerificationCodeService.verify(data: VerificationAssociated, throwIfInvalid: Boolean = false): Boolean {
    val verification = data.verification
    if (verification == null) {
        if (throwIfInvalid) {
            throw InvalidVerificationCodeException("Verification code is null")
        }
        return false
    }
    return this.verifyCode(verification.code, verification.token, throwIfInvalid)
}