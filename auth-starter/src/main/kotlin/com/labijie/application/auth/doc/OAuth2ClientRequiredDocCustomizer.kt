package com.labijie.application.auth.doc

import com.labijie.application.auth.AuthErrors.INVALID_CLIENT
import com.labijie.application.doc.DocUtils.addErrorResponse
import com.labijie.infra.oauth2.filter.ClientRequired
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.GlobalOperationCustomizer
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class OAuth2ClientRequiredDocCustomizer : GlobalOperationCustomizer {

    override fun customize(
        operation: Operation,
        handlerMethod: HandlerMethod
    ): Operation {

        handlerMethod.getMethodAnnotation(ClientRequired::class.java)?.let {
            operation.addSecurityItem(SecurityRequirement().addList(AuthServerSecuritySchemeNames.CLIENT_AUTHORIZATION))
            //HttpStatus.UNAUTHORIZED
            operation.addErrorResponse("Client Authentication", HttpStatus.UNAUTHORIZED, INVALID_CLIENT)
        }
        return operation
    }
}