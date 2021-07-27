package com.labijie.application.web

import com.labijie.infra.oauth2.Constants
import com.labijie.infra.oauth2.TwoFactorPrincipal
import com.labijie.infra.utils.ifNullOrBlank
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.method.HandlerMethod
import java.io.*
import java.lang.IllegalArgumentException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KClass


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */


private fun HttpServletRequest.getHeaderValue(name: String): String? {
    val headValue = this.getHeader(name) //squid
    return if (!headValue.isNullOrBlank() && !"unKnown".equals(headValue, ignoreCase = true)) {
        headValue.getFirstValue()
    } else null
}

private fun String.getFirstValue(): String {
    val index = this.indexOf(",")
    return if (index != -1) {
        this.substring(0, index)
    } else {
        this
    }
}

fun HttpServletRequest.getRealIp(): String {
    return this.getHeaderValue("X-Forwarded-For")
            ?: this.getHeaderValue("X-Real-IP")
            ?: this.getHeaderValue("WL-Proxy-Client-IP")
            ?: this.remoteAddr.ifNullOrBlank { "0.0.0.0" }
}


fun <T : Any?> T.asRestResponse() = RestResponse(this)

private val HttpServletRequest.isFormPost: Boolean
    get() {
        val contentType = this.contentType
        return contentType != null && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) &&
                HttpMethod.POST.matches(this.method)
    }


private fun getBodyFromServletRequestParameters(
        request: HttpServletRequest,
        charset: Charset = Charsets.UTF_8
): InputStream {
    val bos = ByteArrayOutputStream(1024)
    OutputStreamWriter(bos, charset).use { writer ->
        val form = request.parameterMap
        val nameIterator = form.keys.iterator()
        while (nameIterator.hasNext()) {
            val name = nameIterator.next()
            val values = listOf(*(form[name] ?: arrayOf()))
            val valueIterator = values.iterator()
            while (valueIterator.hasNext()) {
                val value = valueIterator.next()
                writer.write(URLEncoder.encode(name, charset.name()))
                if (value != null) {
                    writer.write("=")
                    writer.write(URLEncoder.encode(value, charset.name()))
                    if (valueIterator.hasNext()) {
                        writer.write("&")
                    }
                }
            }
            if (nameIterator.hasNext()) {
                writer.append('&')
            }
        }
        writer.flush()
    }

    return ByteArrayInputStream(bos.toByteArray())
}

@Throws(IOException::class)
fun HttpServletRequest.getBody(): InputStream {
    if (this.isFormPost) {
        return getBodyFromServletRequestParameters(this)
    } else {
        return this.inputStream
    }
}

fun HttpServletRequest.toPrettyString(body: ByteArray): String {
    //body 只能读取一次，所以必须从外面传入
    val builder = StringBuilder()
    builder.appendLine("Scheme: ${this.scheme}")
    builder.appendLine("Path: ${this.requestURI}")
    builder.appendLine("Query: ${this.queryString}")
    builder.appendLine("Method: ${this.method}")
    builder.appendLine("Request IPAddress: ${this.getRealIp()}")
    builder.appendLine("Headers: ")
    this.headerNames.asSequence().forEach {
        builder.appendLine("   $it=${this.getHeader(it)}")
    }
    if (this.contentLength > 0) {
        builder.appendLine("Body: ")
        builder.appendLine(body.toString(Charset.forName(this.characterEncoding.ifNullOrBlank { Charsets.UTF_8.name() })))
    }
    return builder.toString()
}

val HttpServletRequest.isFromAlipay: Boolean
    get() {
        return this.getHeader("user-agent").orEmpty().contains("AlipayClient", ignoreCase = true)
    }

val HttpServletRequest.isFromWechat: Boolean
    get() {
        return this.getHeader("user-agent").orEmpty().contains("MicroMessenger", ignoreCase = true)
    }

fun HandlerMethod.hasAnnotationOnMethodOrClass(annotation: KClass<out Annotation>): Boolean {
    return this.hasMethodAnnotation(annotation.java) || AnnotationUtils.isAnnotationDeclaredLocally(annotation.java, this.method.declaringClass)
}

private const val ROLE_PREFIX = Constants.ROLE_AUTHORITY_PREFIX

fun roleAuthority(role: String): SimpleGrantedAuthority {
    return SimpleGrantedAuthority("$ROLE_PREFIX${role}")
}

fun TwoFactorPrincipal.hasRole(role: String): Boolean {
    if (role.startsWith(ROLE_PREFIX)) {
        throw IllegalArgumentException(
                "role name should not start with '$ROLE_PREFIX' since it is automatically inserted. Got '$role'"
        )
    }

    if (role.isBlank()) {
        return false
    }
    return this.authorities.any {
        it.authority.contains("$ROLE_PREFIX$role")
    }
}

fun TwoFactorPrincipal.hasAnyRole(vararg roles: String): Boolean {
    if (roles.isEmpty()) {
        return false
    }
    return this.authorities.any {
        roles.map { r->"$ROLE_PREFIX$r" }.contains(it.authority)
    }
}

object WebUtils {
    val currentRequest: String
        get() {
            val requestAttributes = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)
                    ?: throw RuntimeException("Get ServletRequestAttributes fault.")
            return requestAttributes.request.getRealIp()
        }
}

fun ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry.antMatchers(vararg antPatterns: String, ignoreCase: Boolean = false, method: HttpMethod? = null): ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl {
    val matchers = antPatterns.toList().map {
        AntPathRequestMatcher(it, method?.name, !ignoreCase)
    }.toTypedArray()
    return this.requestMatchers(*matchers)
}
