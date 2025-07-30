package com.labijie.application.service

import com.labijie.application.component.IHumanChecker
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.crypto.DesUtils
import com.labijie.application.web.InvalidRequestArgumentsException

/**
 * @author Anders Xiao
 * @date 2023-12-03
 */
class CaptchaHumanChecker(private val applicationProperties: ApplicationCoreProperties) : IHumanChecker {

    override fun clientStampRequired(): Boolean {
        return true
    }

    override fun check(token: String, clientStamp: String?, clientIp: String?): Boolean {
        if(clientStamp.isNullOrBlank()) {
            throw InvalidRequestArgumentsException("client")
        }

        if(token.isNotBlank() && clientStamp.isNotBlank()) {
            return DesUtils.verifyToken(clientStamp, token.lowercase(), applicationProperties.desSecret)
        }
        return false
    }
}