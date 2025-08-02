package com.labijie.application.auth.doc

import com.labijie.application.auth.AuthErrors.INVALID_CLIENT
import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.application.doc.DocUtils.addErrorResponse
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.GlobalOperationCustomizer
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

/**
 * @author Anders Xiao
 * @date 2025/8/1
 */
class AuthServerOperationCustomizer : GlobalOperationCustomizer {
    override fun customize(
        operation: Operation,
        handlerMethod: HandlerMethod?
    ): Operation? {
        val containIdToken = handlerMethod?.methodParameters?.any {
            it.parameter.getDeclaredAnnotation(ServerIdToken::class.java) != null
        } ?: false

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