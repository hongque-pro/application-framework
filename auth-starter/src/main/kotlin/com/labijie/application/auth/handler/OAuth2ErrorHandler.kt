//package com.labijie.application.auth.handler
//
//import com.fasterxml.jackson.annotation.JsonIgnore
//import com.fasterxml.jackson.annotation.JsonProperty
//import com.labijie.application.ApplicationErrors
//import com.labijie.application.ErrorCodedException
//import com.labijie.application.auth.handler.OAuth2ErrorHandler.OAuth2Error.Companion.UnhandledError
//import com.labijie.application.getCauseFromChain
//import com.labijie.infra.oauth2.error.IOAuth2ExceptionHandler
//import com.labijie.infra.utils.logger
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.security.core.AuthenticationException
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
//import org.springframework.stereotype.Component
//import org.springframework.web.HttpRequestMethodNotSupportedException
//
///**
// * Created with IntelliJ IDEA.
// * @author Anders Xiao
// * @date 2019-09-05
// */
//class OAuth2ErrorHandler : IOAuth2ExceptionHandler {
//
//    override fun translate(e: Exception): ResponseEntity<OAuth2Exception> {
//        val errorCodeException = e.getCauseFromChain(ErrorCodedException::class)
//        if (errorCodeException != null) {
//            if (errorCodeException.error != ApplicationErrors.UnhandledError) {
//                val cex = OAuth2Error(errorCodeException.error, errorCodeException.message.orEmpty())
//                return ResponseEntity(cex, HttpStatus.UNAUTHORIZED)
//            } else {
//                return ResponseEntity(UnhandledError, HttpStatus.INTERNAL_SERVER_ERROR)
//            }
//        }
//
//        val oAuth2Exception = e.getCauseFromChain(OAuth2Exception::class)
//        if (oAuth2Exception != null) {
//            return ResponseEntity(OAuth2Error(oAuth2Exception.oAuth2ErrorCode, oAuth2Exception.message), HttpStatus.UNAUTHORIZED)
//        }
//
//        val authenticationException = e.getCauseFromChain(AuthenticationException::class)
//        if (authenticationException != null) {
//            val cex = OAuth2Error(OAuth2Exception.INVALID_GRANT)
//            return ResponseEntity(cex, HttpStatus.UNAUTHORIZED)
//        }
//
//        val accessDeniedException = e.getCauseFromChain(AccessDeniedException::class)
//        if (accessDeniedException != null) {
//            val cex = OAuth2Error(OAuth2Exception.ACCESS_DENIED)
//            return ResponseEntity(cex, HttpStatus.UNAUTHORIZED)
//        }
//
//        val methodNotSupportedException = e.getCauseFromChain(HttpRequestMethodNotSupportedException::class)
//        if (methodNotSupportedException != null) {
//            return ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED)
//        }
//
//        logger.error("Unhandled oauth2 error", e)
//        return ResponseEntity(UnhandledError, HttpStatus.INTERNAL_SERVER_ERROR)
//    }
//
//    protected class OAuth2Error(
//        var error: String,
//        errorDescription: String?,
//        private val httpCode: Int = 401
//    ) : OAuth2Exception(errorDescription) {
//
//        companion object {
//            @JvmStatic
//            val UnhandledError: OAuth2Exception =
//                OAuth2Error(ApplicationErrors.UnhandledError, "unhandled oatuh2 error", 500)
//        }
//
//        constructor(errorCode: String):this(errorCode, errorCode)
//
//        override fun getHttpErrorCode() = httpCode
//
//        @get: JsonProperty("description")
//        override val message: String?
//            get() = super.message
//
//        @JsonIgnore
//        override fun getLocalizedMessage(): String {
//            return super.getLocalizedMessage()
//        }
//
//        @JsonIgnore
//        override fun getOAuth2ErrorCode(): String {
//            return this.error
//        }
//    }
//
//}