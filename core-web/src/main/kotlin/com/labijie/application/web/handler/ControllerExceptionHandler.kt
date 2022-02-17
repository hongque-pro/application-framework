package com.labijie.application.web.handler

import com.labijie.application.ApplicationErrors
import com.labijie.application.ApplicationErrors.UnhandledError
import com.labijie.application.ErrorCodedException
import com.labijie.application.ErrorCodedStatusException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.lang.reflect.UndeclaredThrowableException
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@RestControllerAdvice
class ControllerExceptionHandler : Ordered {
    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    private val logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)


    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(request: HttpServletRequest, e: AuthenticationException): ErrorResponse {
        if(e is OAuth2AuthenticationException){
            return ErrorResponse(e.error.errorCode, e.error.description ?: e.error.errorCode)
        }
        return ErrorResponse(UnhandledError, OAuth2ErrorCodes.ACCESS_DENIED)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: MethodArgumentNotValidException): ErrorResponse {

        val violations = e.bindingResult.allErrors

        val argErrors = violations.map {
            val field = it as? FieldError
            if (field != null) {
                val key = if (field.objectName == e.bindingResult.objectName) field.field else "${field.objectName}.${field.field}"
                key to it.defaultMessage.orEmpty()
            } else {
                it.objectName to it.defaultMessage.orEmpty()
            }

        }.toMap()

        return InvalidParameterResponse(
            ApplicationErrors.BadRequestParameter,
            "invalid argument",
            argErrors
        )
    }

    @ExceptionHandler(ErrorCodedStatusException::class)
    fun handleErrorCodedStatusException(request: HttpServletRequest, e: ErrorCodedStatusException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(e.error, e.message), e.status)
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
        val msg = violations.map {
            it.propertyPath.toString() to it.message
        }.toMap()

        return InvalidParameterResponse(
            ApplicationErrors.BadRequestParameter,
            "invalid argument",
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



    @ExceptionHandler(ErrorCodedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleCodedException(request: HttpServletRequest, e: ErrorCodedException): ErrorResponse {
        if(e.error == UnhandledError){
            logger.error("""
            ------------------------------------------------------
            ------------ Controller Unhandled Error ------------
            ------------------------------------------------------
            HTTP Method: ${request.method}
            ${request.requestURI}
            """, e)
        }
        return ErrorResponse(e.error, e.message, e.getDetails())
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnhandledException(request: HttpServletRequest, e: Throwable): ErrorResponse {
        val error = when(e){
            is UndeclaredThrowableException -> e.undeclaredThrowable ?: e
            else -> e
        }

        logger.error("""
            ------------------------------------------------------
            ------------ Controller Unhandled Error ------------
            ------------------------------------------------------
            HTTP Method: ${request.method} 
            ${request.requestURI}
            """, error)
        return ErrorResponse(UnhandledError, "system error")
    }
}