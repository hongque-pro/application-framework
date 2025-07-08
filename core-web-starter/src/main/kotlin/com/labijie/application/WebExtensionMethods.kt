/**
 * @author Anders Xiao
 * @date 2024-07-29
 */
package com.labijie.application

import com.labijie.infra.utils.ifNullOrBlank
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpRequest
import org.springframework.security.web.util.UrlUtils


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