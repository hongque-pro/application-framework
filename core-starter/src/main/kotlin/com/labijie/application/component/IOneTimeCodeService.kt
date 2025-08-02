package com.labijie.application.component

//fun IOneTimeCodeService.verify(data: VerificationAssociated, throwIfInvalid: Boolean = false): Boolean {
//    val verification = data.verification
//    if (verification == null) {
//        if (throwIfInvalid) {
//            throw InvalidOneTimeCodeException("Verification code is null")
//        }
//        return false
//    }
//    return this.verifyCode(verification.code, verification.stamp, throwIfInvalid)
//}