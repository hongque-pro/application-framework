package com.labijie.application.component.impl

import com.labijie.application.component.IVerificationCodeService
import com.labijie.application.configuration.VerificationCodeProperties
import com.labijie.application.exception.InvalidVerificationCodeException
import com.labijie.infra.security.IRfc6238TokenService
import com.labijie.infra.security.Rfc6238TokenService

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class DefaultVerificationCodeService(
    private val properties: VerificationCodeProperties,
    private val rfc6238TokenService: IRfc6238TokenService): IVerificationCodeService {

    constructor() : this(VerificationCodeProperties(), Rfc6238TokenService())

    override fun verifyCode(
        code: String,
        verificationToken: String,
        throwIfInvalid: Boolean
    ): Boolean {
        val result = this.rfc6238TokenService.validateCodeString(code, verificationToken, properties.expiration)
        if (throwIfInvalid && !result) {
            throw InvalidVerificationCodeException()
        }
        return result
    }

    override fun generateCode(verificationToken: String): String {
        val r = this.rfc6238TokenService.generateCode(verificationToken, properties.expiration)
        return r.toString()
    }
}