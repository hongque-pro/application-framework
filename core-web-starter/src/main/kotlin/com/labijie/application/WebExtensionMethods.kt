/**
 * @author Anders Xiao
 * @date 2024-07-29
 */
package com.labijie.application

import com.labijie.infra.utils.ifNullOrBlank
import jakarta.servlet.http.HttpServletRequest


fun HttpServletRequest.printFriendlyString(bodyBytes: ByteArray? = null, title: String = "Coming HTTP Request", appending: ((StringBuilder)->Unit)? = null): String {
    val stringBuilder = StringBuilder()
    val t = title.ifNullOrBlank { "Coming HTTP Request" }
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
        stringBuilder.appendLine(bodyBytes.toString(Charsets.UTF_8).ifNullOrBlank { "<null>" })
    }
    appending?.invoke(stringBuilder)
    stringBuilder.appendLine("---------------- End $t ----------------")

    return stringBuilder.toString()
}