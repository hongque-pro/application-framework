package com.labijie.application.model


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
open class VerificationAssociated {
    var verification: VerificationRequest? = null

    fun setSmsCode(code: String, token: String) {
        verification = VerificationRequest(token, code)
    }
}