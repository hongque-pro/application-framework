/**
 * @author Anders Xiao
 * @date 2024-07-29
 */
package com.labijie.application

import com.labijie.application.exception.InvalidOneTimeCodeException
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.model.OneTimeCodeVerifyRequest
import com.labijie.application.model.OneTimeCodeVerifyResult.Companion.getInputOrThrow
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.web.interceptor.OneTimeCodeInterceptor
import com.labijie.infra.utils.ifNullOrBlank
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpRequest
import org.springframework.security.web.util.UrlUtils

fun HttpServletRequest.getOneTimeCodeInRequest(): OneTimeCodeVerifyRequest? {
    val code = this.getHeader(OneTimeCodeInterceptor.CODE_KEY) ?: this.getParameter(OneTimeCodeInterceptor.CODE_KEY)
    val stamp = this.getHeader(OneTimeCodeInterceptor.STAMP_KEY) ?: this.getParameter(OneTimeCodeInterceptor.STAMP_KEY)

    if(code.isNullOrBlank() || stamp.isNullOrBlank()) {
        return null
    }
    return OneTimeCodeVerifyRequest(code, stamp)
}

fun IOneTimeCodeService.verify(request: OneTimeCodeVerifyRequest, user: User, throwInfInvalid: Boolean = true): Boolean {
    val result = this.verifyCode(request.code, request.stamp)
    if(!result.success && throwInfInvalid) {
        throw InvalidOneTimeCodeException()
    }
    val input = result.getInputOrThrow()

    var reason: String? = null
    val  valid = when(input.channel) {
        OneTimeCodeTarget.Channel.Phone-> {
            val v = input.contact == user.fullPhoneNumber
            if(!v) {
                reason = InvalidOneTimeCodeException.REASON_INVALID_CONTACT
            }
            v
        }
        OneTimeCodeTarget.Channel.Email -> {
            val v = (input.contact == user.email)
            reason = InvalidOneTimeCodeException.REASON_INVALID_CONTACT
            v
        }
    }
    if(!valid && throwInfInvalid) {
        throw InvalidOneTimeCodeException()
    }
    return valid
}

fun IOneTimeCodeService.verify(request: HttpServletRequest, user: User, throwInfInvalid: Boolean = true): Boolean {
    val code = request.getOneTimeCodeInRequest()
    if(code == null) {
        if(throwInfInvalid) throw InvalidOneTimeCodeException()
        return false
    }
    return verify(code, user, throwInfInvalid)
}


fun HttpServletRequest.printFriendlyString(bodyBytes: ByteArray? = null, title: String = "Coming HTTP Request", appending: ((StringBuilder)->Unit)? = null): String {
    val stringBuilder = StringBuilder()
    val t = title.ifBlank { "Coming HTTP Request" }
    stringBuilder.appendLine()
    stringBuilder.appendLine("---------------- Begin $t ----------------")
    stringBuilder.appendLine("URL: ${this.requestURI}")
    stringBuilder.appendLine("Method: ${this.method}")
    stringBuilder.appendLine("Headers: ")
    val headers = this.headerNames
    while (headers.hasMoreElements()) {
        val name = headers.nextElement()
       val headerValue = this.getHeader(name)
        stringBuilder.appendLine("  ${name}: ${headerValue ?: "<null>"}")
    }
    if(!this.method.equals("get", ignoreCase = true) && bodyBytes != null) {
        stringBuilder.appendLine("Body:")
        stringBuilder.appendLine(bodyBytes.toString(Charsets.UTF_8).ifBlank { "<null>" })
    }
    appending?.invoke(stringBuilder)
    stringBuilder.appendLine("---------------- End $t ----------------")

    return stringBuilder.toString()
}

fun HttpServletRequest.getUrlWithoutQueryString(): String {
    val schema = this.getHeader("X-Forwarded-Proto").ifNullOrBlank { this.scheme }

    val host = this.getHeader("X-Forwarded-Host").ifNullOrBlank { this.serverName }

    val port = if(this.getHeader("X-Forwarded-Proto").isNullOrBlank()) this.serverPort else (if(schema.equals("https", ignoreCase = true)) 443 else 80)

    return UrlUtils.buildFullRequestUrl(schema, host, port, this.requestURI, null)
}

fun HttpServletRequest.getOriginProtocol(): String {
    val schema = this.getHeader("X-Forwarded-Proto").ifNullOrBlank { this.scheme }
    return schema
}

fun HttpRequest.getOriginProtocol() : String? {
    val schema = this.headers.get("X-Forwarded-Proto")?.toString()
    return schema
}