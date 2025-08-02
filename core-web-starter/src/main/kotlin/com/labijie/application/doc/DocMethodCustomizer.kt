package com.labijie.application.doc

import com.labijie.application.ApplicationErrors
import com.labijie.application.component.IHumanChecker
import com.labijie.application.doc.DocUtils.addErrorResponse
import com.labijie.application.doc.DocUtils.appendDescription
import com.labijie.application.isEnabled
import com.labijie.application.web.annotation.HttpCache
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.application.web.annotation.OneTimeCodeVerify
import com.labijie.application.web.interceptor.HumanVerifyInterceptor
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.GlobalOperationCustomizer
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@Component
class DocMethodCustomizer(private val humanChecker: IHumanChecker) : GlobalOperationCustomizer {

    override fun customize(
        operation: Operation,
        handlerMethod: HandlerMethod
    ): Operation {

        processHumanVerifyAnnotation(handlerMethod, operation)
        processOneTimeCodeVerifyAnnotation(handlerMethod, operation)
        processHttpCache(handlerMethod, operation)

        return operation
    }

    private fun processHttpCache(
        handlerMethod: HandlerMethod,
        operation: Operation
    ) {
        handlerMethod.getMethodAnnotation(HttpCache::class.java)?.let {
            if(it.maxAge > 0) {
                val duration = Duration.of(it.maxAge, it.unit.toChronoUnit())
                operation.appendDescription(
                    "`Cache-Control: public, max-age=${duration.toSeconds()}`"
                )
            }

        }
    }

    private fun processHumanVerifyAnnotation(
        handlerMethod: HandlerMethod,
        operation: Operation
    ) {
        handlerMethod.getMethodAnnotation(HumanVerify::class.java)?.let {

            operation.addSecurityItem(SecurityRequirement().addList(SecuritySchemeNames.HUMAN_VERIFY_TOKEN))
            if(humanChecker.clientStampRequired()) {
                operation.addSecurityItem(SecurityRequirement().addList(SecuritySchemeNames.HUMAN_VERIFY_STAMP))
            }
            val checkerStatus = if(humanChecker.isEnabled) "ON" else "`OFF`"
            operation.appendDescription("This method is protected by `CAPTCHA` (a human verification step), the verification status: ${checkerStatus}.")
            operation.addErrorResponse("Human Verify", HumanVerifyInterceptor.statusOnFailure, ApplicationErrors.RobotDetected)

        }
    }

    private fun processOneTimeCodeVerifyAnnotation(
        handlerMethod: HandlerMethod,
        operation: Operation
    ) {
        handlerMethod.getMethodAnnotation(OneTimeCodeVerify::class.java)?.let {

            operation.addSecurityItem(SecurityRequirement().addList(SecuritySchemeNames.ONE_TIME_CODE))
            operation.addSecurityItem(SecurityRequirement().addList(SecuritySchemeNames.ONE_TIME_STAMP))
            operation.appendDescription("To ensure account security, this method requires verification using a time-based one-time password (`TOTP`).")
            operation.addErrorResponse("TOTP", HumanVerifyInterceptor.statusOnFailure, ApplicationErrors.RobotDetected)

        }
    }
}