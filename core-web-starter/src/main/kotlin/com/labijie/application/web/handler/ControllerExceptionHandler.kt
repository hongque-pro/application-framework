package com.labijie.application.web.handler

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.JsonMappingException
import com.labijie.application.*
import com.labijie.application.ApplicationErrors.UnhandledError
import com.labijie.infra.utils.ifNullOrBlank
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.lang.reflect.UndeclaredThrowableException
import kotlin.collections.toMap

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@RestControllerAdvice
class ControllerExceptionHandler(private val messageSource: MessageSource) : Ordered {
    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    private val logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)


    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(request: HttpServletRequest, e: AuthenticationException): ErrorResponse {
        if (e is OAuth2AuthenticationException) {
            return ErrorResponse(
                e.error.errorCode,
                localeErrorMessage(e.error.errorCode, e.error.description ?: e.error.errorCode)
            )
        }
        return ErrorResponse(OAuth2ErrorCodes.ACCESS_DENIED)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: HandlerMethodValidationException): ErrorResponse {
        val argErrors =
            e.allValidationResults.filter { !it.methodParameter.parameterName.isNullOrBlank() }.associate { result ->
                result.methodParameter.parameterName!! to result.resolvableErrors.map {
                    it.defaultMessage
                }.joinToString("\n")
            }

        return InvalidParameterResponse(
            ApplicationErrors.BadRequestParameter,
            argErrors
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: MethodArgumentNotValidException): ErrorResponse {

        val violations = e.bindingResult.allErrors

        val argErrors = violations.map {
            val field = it as? FieldError
            if (field != null) {
                val key =
                    if (field.objectName == e.bindingResult.objectName) field.field else "${field.objectName}.${field.field}"
                key to it.defaultMessage.orEmpty()
            } else {
                it.objectName to it.defaultMessage.orEmpty()
            }

        }.toMap()

        return InvalidParameterResponse(
            ApplicationErrors.BadRequestParameter,
            argErrors
        )
    }


//    @ExceptionHandler(AuthenticationException::class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    fun handle(request: HttpServletRequest, e: AuthenticationException): ErrorResponse {
//        if(logger.isDebugEnabled){
//            logger.warn("Authentication error handled.", e)
//        }
//        return ErrorResponse(OAuth2Exception.INVALID_GRANT, e.message)
//    }


//    @ExceptionHandler(HttpMediaTypeNotAcceptableException::class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    fun handle(request:HttpServletRequest, e: HttpMediaTypeNotAcceptableException): ErrorResponse {
//        return ErrorResponse(ApplicationErrors.MediaTypeNotAcceptable, e.message)
//    }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: ConstraintViolationException): ErrorResponse {

        val violations = e.constraintViolations
        val msg = violations.associate {
            it.propertyPath.toString() to it.message
        }

        return InvalidParameterResponse(
            ApplicationErrors.BadRequestParameter,
            msg
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: MissingServletRequestParameterException): ErrorResponse {
        return MissingParameterResponse(
            ApplicationErrors.RequestParameterMissed,
            e.localizedMessage,
            e.parameterName
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: MethodArgumentTypeMismatchException): ErrorResponse {
        return MissingParameterResponse(
            ApplicationErrors.BadRequestParameter,
            e.localizedMessage,
            e.parameter.parameterName.orEmpty()
        )
    }

    @ExceptionHandler(ErrorCodedStatusException::class)
    fun handleErrorCodedStatusException(
        request: HttpServletRequest,
        e: ErrorCodedStatusException
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(e.error, e.localizedMessage), e.status)
    }

    @ExceptionHandler(ErrorCodedException::class)
    @ResponseStatus(HttpStatus.OK)
    fun handleCodedException(request: HttpServletRequest, e: ErrorCodedException): ErrorResponse {
        return ErrorResponse(e.error, e.localizedMessage, e.getDetails())
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableExceptionHandler(
        request: HttpServletRequest,
        e: HttpMessageNotReadableException
    ): ResponseEntity<ErrorResponse> {
        val jsonException = e.getCauseFromChain(JacksonException::class)
        if (jsonException != null) {
            val details = if (jsonException is JsonMappingException) {
                mutableMapOf(
                    jsonException.pathReference to jsonException.localizedMessage.ifNullOrBlank { jsonException.message ?: "Json deserialize failed." }
                )
            } else null
            val error = ErrorResponse(ApplicationErrors.InvalidRequestFormat, details = details)
            return ResponseEntity(error, HttpStatus.BAD_REQUEST)
        }

        val response = handleUnhandledException(request, e)
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(request: HttpServletRequest, e: NoHandlerFoundException): ErrorResponse {
        val details = mapOf(
            "http_method" to e.httpMethod,
            "request_url" to e.requestURL
        )
        return ErrorResponse("HTTP_404", e.localizedMessage, details)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnhandledException(request: HttpServletRequest, e: Throwable): ErrorResponse {
        val error = when (e) {
            is UndeclaredThrowableException -> e.undeclaredThrowable ?: e
            else -> e
        }

        logger.error(
            """
            ------------------------------------------------------
            ------------ Controller Unhandled Error ------------
            ------------------------------------------------------
            HTTP Method: ${request.method} 
            Request URI: ${request.requestURI}
            """, error
        )
        return ErrorResponse(UnhandledError)
    }
}