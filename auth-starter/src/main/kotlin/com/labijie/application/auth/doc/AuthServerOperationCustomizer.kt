package com.labijie.application.auth.doc

import com.labijie.application.auth.AuthErrors.INVALID_CLIENT
import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.controller.RegisterController
import com.labijie.application.doc.DocUtils.addErrorResponse
import com.labijie.application.doc.DocUtils.addTotpSecuritySchema
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.GlobalOperationCustomizer
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

/**
 * @author Anders Xiao
 * @date 2025/8/1
 */
class AuthServerOperationCustomizer(private val authProperties: AuthProperties) : GlobalOperationCustomizer {
    override fun customize(
        operation: Operation,
        handlerMethod: HandlerMethod?
    ): Operation? {
        val containIdToken = handlerMethod?.methodParameters?.any {
            it.parameter.getDeclaredAnnotation(ServerIdToken::class.java) != null
        } ?: false

        if(authProperties.registerEndpoint.verifyEmailOrPhone &&
            handlerMethod?.beanType == RegisterController::class.java &&
            handlerMethod.method.name == RegisterController::register.name) {
            operation.addTotpSecuritySchema()
            //@Operation(description = "If the password is empty, `TOTP` verification will be required.")
        }

        if (containIdToken) {
            operation.addSecurityItem(SecurityRequirement().addList(AuthServerSecuritySchemeNames.SERVER_ID_TOKEN))
            //HttpStatus.UNAUTHORIZED
            operation.addErrorResponse(
                "Id Token",
                HttpStatus.UNAUTHORIZED,
                INVALID_CLIENT
            )

        }
        return operation
    }
}