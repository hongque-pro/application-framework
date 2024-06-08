package com.labijie.application.service

import com.labijie.application.component.IHumanChecker
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.crypto.DesUtils

/**
 * @author Anders Xiao
 * @date 2023-12-03
 */
class CaptchaHumanChecker(private val applicationProperties: ApplicationCoreProperties) : IHumanChecker {
    override fun check(token: String, clientStamp: String, clientIp: String): Boolean {
        if(token.isNotBlank() && clientStamp.isNotBlank()) {
            return DesUtils.verifyToken(clientStamp, token.lowercase(), applicationProperties.desSecret)
        }
        return false
    }
}