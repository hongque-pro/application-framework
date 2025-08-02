package com.labijie.application.service.impl

import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.configuration.OneTimeCodeProperties
import com.labijie.application.crypto.DesUtils
import com.labijie.application.exception.InvalidOneTimeCodeException
import com.labijie.application.model.OneTimeCode
import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.model.OneTimeCodeVerifyResult
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.toEnum
import com.labijie.infra.security.IRfc6238TokenService
import com.labijie.infra.security.Rfc6238TokenService
import com.labijie.infra.utils.ShortId

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class DefaultOnetimeCodeService(
    private val applicationCoreProperties: ApplicationCoreProperties,
    private val properties: OneTimeCodeProperties,
    private val rfc6238TokenService: IRfc6238TokenService
): IOneTimeCodeService {

    constructor() : this(ApplicationCoreProperties(), OneTimeCodeProperties(), Rfc6238TokenService())

    override fun verifyCode(
        code: String,
        stamp: String,
        channel: OneTimeCodeTarget.Channel?,
        contract: String?,
        throwIfInvalid: Boolean
    ): OneTimeCodeVerifyResult {

        var result = this.rfc6238TokenService.validateCodeString(code, stamp, properties.expiration)
        val code = decodeSource(stamp)
        val source = if(result) {
            if(code == null) {
                result = false
            }
            else {
                val validSource = contract?.let {
                    code.contact == contract
                } ?: true

                val validType = contract?.let {
                    code.channel == channel
                } ?: true

                result = validSource && validType
            }
            code
        }else {
            null
        }

        if (throwIfInvalid && !result) {
            throw InvalidOneTimeCodeException()
        }

        return OneTimeCodeVerifyResult(result, source)
    }

    private fun encodeSource(source: OneTimeCodeTarget): String {

        val key = "${source.channel.toString().lowercase()}:${source.contact}:${ShortId.newId()}"
        return DesUtils.encrypt(key, applicationCoreProperties.desSecret)
    }

    private fun decodeSource(encodedString: String): OneTimeCodeTarget? {
        val value = DesUtils.decrypt(encodedString)
        val segments = value.split(":")
        if(segments.size != 3) {
            return null
        }

        val type = segments[0].toEnum<OneTimeCodeTarget.Channel>(true)
        return OneTimeCodeTarget().apply {
            channel = type
            contact = segments[1]
        }
    }

    override fun decodeStamp(stamp: String): OneTimeCodeTarget {
        return decodeSource(stamp) ?: throw InvalidOneTimeCodeException()
    }


    override fun generateCode(source: OneTimeCodeTarget): OneTimeCode {

        val stamp = encodeSource(source)

        val code = this.rfc6238TokenService.generateCode(stamp, properties.expiration)
        return OneTimeCode(code.toString(), stamp, properties.expiration)
    }
}