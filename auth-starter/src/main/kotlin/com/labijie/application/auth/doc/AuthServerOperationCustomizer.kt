package com.labijie.application.auth.doc

import com.labijie.application.JsonMode
import com.labijie.application.auth.AuthErrors
import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.application.auth.configuration.AuthProperties
import com.labijie.application.auth.controller.RegisterController
import com.labijie.application.configuration.ApplicationWebProperties
import com.labijie.application.doc.DocUtils.addErrorResponse
import com.labijie.application.doc.DocUtils.addTotpSecuritySchema
import com.labijie.application.doc.DocUtils.appendDescription
import com.labijie.application.localeErrorMessage
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.oauth2.mvc.OAuth2ClientLoginResponse
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.security.SecurityRequirement
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle
import org.springdoc.core.customizers.GlobalOperationCustomizer
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

/**
 * @author Anders Xiao
 * @date 2025/8/1
 */
class AuthServerOperationCustomizer(
    private val webProperties: ApplicationWebProperties,
    private val authProperties: AuthProperties
) : GlobalOperationCustomizer {
    override fun customize(
        operation: Operation?,
        handlerMethod: HandlerMethod?
    ): Operation? {


        if (handlerMethod != null && operation != null) {
            if (authProperties.registerEndpoint.verifyEmailOrPhone &&
                handlerMethod.beanType == RegisterController::class.java &&
                handlerMethod.method.name == RegisterController::register.name
            ) {
                operation.addTotpSecuritySchema()
                //@Operation(description = "If the password is empty, `TOTP` verification will be required.")
            }

            processServerIdAnnotation(handlerMethod, operation)
            processOAuth2ClientLoginResponse(handlerMethod, operation)

        }

        return operation
    }

    private fun processServerIdAnnotation(
        handlerMethod: HandlerMethod,
        operation: Operation
    ) {
        val serverIdAnnotation = handlerMethod.methodParameters.firstOrNull {
            it.parameter.getDeclaredAnnotation(ServerIdToken::class.java) != null
        }?.parameter?.getDeclaredAnnotation(ServerIdToken::class.java)


        if (serverIdAnnotation != null) {
            val options = if (serverIdAnnotation.required) "Required" else "Optional"
            operation.addSecurityItem(SecurityRequirement().addList(AuthServerSecuritySchemeNames.SERVER_ID_TOKEN))
            operation.appendDescription("This method requires a server OIDC token (`id-token`) from server (`${options}`).")
            //HttpStatus.UNAUTHORIZED
            operation.addErrorResponse(
                "Id Token",
                HttpStatus.UNAUTHORIZED,
                AuthErrors.INVALID_TOKEN
            )
        }
    }


    private fun processOAuth2ClientLoginResponse(
        handlerMethod: HandlerMethod,
        operation: Operation
    ) {

        if (OAuth2ClientLoginResponse::class.java.isAssignableFrom(handlerMethod.returnType.parameterType)) {
            operation.responses["200"]?.let { response ->
                response.description =
                    "If OAuth2 account not found, got error code: `${AuthErrors.OAUTH2_USER_NOT_REGISTERED}`"
                response.content.get("*/*")?.let {
                    it.examples = mapOf(
                        "success" to Example().apply {
                            summary = "OK"
                            value = JacksonHelper.serializeAsJsonNode(
                                OAuth2ClientLoginSuccess().apply {
                                    accessToken.accessToken = "string"
                                    accessToken.expiresIn = 0
                                    accessToken.tokenType = "Bearer"
                                    accessToken.userId = "0"
                                    accessToken.twoFactorGranted = false
                                    accessToken.username = "string"
                                    accessToken.refreshToken = "string"
                                    accessToken.authorities = mutableListOf("role1", "role2", "role3")
                                    accessToken.details = hashMapOf(
                                        "additionalProp1" to "string",
                                        "additionalProp2" to false,
                                        "additionalProp3" to 0
                                    )
                                },
                                webProperties.jsonMode == JsonMode.JAVASCRIPT
                            )
                        },
                        "error" to Example().apply {
                            summary = "OAuth2 Account Not Registered"
                            value = OAuth2ClientLoginError().also {
                                e ->
                                e.error = AuthErrors.OAUTH2_USER_NOT_REGISTERED
                                e.error_description = "string"
                                e.idToken = "string"
                            }
                        }
                    )
                }
//                response.content = Content().apply {
//                    this.addMediaType("*/*", MediaType().apply {
//                       schema = Schema<Any>().apply {
//                           oneOf = listOf(
//                               Schema<OAuth2ClientLoginSuccess>().apply {
//                                   setDefault(OAuth2ClientLoginSuccess())
//                               },
//                               Schema<OAuth2ClientLoginError>().apply {
//                                   setDefault(OAuth2ClientLoginError().apply {
//                                       error = AuthErrors.OAUTH2_USER_NOT_REGISTERED
//                                   })
//                               }
//                           )
//                       }
//                        examples = mapOf(
//                            "success" to Example().apply {
//                                summary = "Successful login"
//                                value = OAuth2ClientLoginSuccess()
//                            },
//                            "error" to Example().apply {
//                                summary = "OAuth2 account not registered"
//                                value = OAuth2ClientLoginError().apply {
//                                    error = AuthErrors.OAUTH2_USER_NOT_REGISTERED
//                                }
//                            }
//                        )
//                    })
//                }
            }

        }
    }
}