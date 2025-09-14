package com.labijie.application.dapr.components

import com.labijie.application.ObjectUtils.parseQueryStringDecoded
import io.dapr.client.DaprClient
import io.dapr.client.DaprHttp
import io.dapr.client.domain.HttpExtension
import io.dapr.client.domain.InvokeMethodRequest
import io.dapr.utils.TypeRef
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.service.invoker.HttpExchangeAdapter
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.UriTemplate
import java.util.function.Consumer

/**
 * @author Anders Xiao
 * @date 2025/9/9
 */
class DaprHttpExchangeAdapter(
    private val daprClient: DaprClient,
    private val appId: String
) : HttpExchangeAdapter {

    companion object {
        @JvmStatic
        fun create(daprClient: DaprClient, appId: String): DaprHttpExchangeAdapter {
            return DaprHttpExchangeAdapter(daprClient, appId)
        }
    }

    private fun HttpMethod.getDaprMethod(): DaprHttp.HttpMethods {
        return when (this.name().uppercase()) {
            "GET" -> DaprHttp.HttpMethods.GET
            "HEAD" -> DaprHttp.HttpMethods.HEAD
            "POST" -> DaprHttp.HttpMethods.POST
            "PUT" -> DaprHttp.HttpMethods.PUT
            //"PATCH" -> DaprHttp.HttpMethods.PATCH
            "DELETE" -> DaprHttp.HttpMethods.DELETE
            "OPTIONS" -> DaprHttp.HttpMethods.OPTIONS
            "TRACE" -> DaprHttp.HttpMethods.TRACE
            else -> DaprHttp.HttpMethods.valueOf(this.name())
        }
    }

    private fun newRequest(values: HttpRequestValues): InvokeMethodRequest {
        val httpMethod = values.httpMethod
        requireNotNull(httpMethod, { "HttpMethod is required" })


        val uri = if (values.uri != null) {
            values.uri
        } else if (values.uriTemplate != null) {
            val uriBuilderFactory = values.uriBuilderFactory
            if (uriBuilderFactory != null) {
                val uri = uriBuilderFactory.expand(values.uriTemplate!!, values.uriVariables)
                uri
            } else {
                UriTemplate(values.uriTemplate!!).expand(values.uriVariables)
            }
        } else {
            throw IllegalStateException("Neither full URL nor URI template")
        }


        val headers = values.headers.map {
            it.key to it.value.joinToString(",")
        }.toMap().toMutableMap()

        if (!values.cookies.isEmpty()) {
            val cookies: MutableList<String>  =mutableListOf()
            values.cookies.forEach { (name: String, cookieValues: MutableList<String>) ->
                cookieValues.forEach(Consumer { value: String? ->
                    val cookie = HttpCookie(name, value)
                    cookies.add(cookie.toString())
                })
            }
            headers.putIfAbsent(HttpHeaders.COOKIE, cookies.joinToString(separator = ";"))
        }

        val queryString = uri.parseQueryStringDecoded()
        val extension = HttpExtension(httpMethod.getDaprMethod(), queryString, headers)

        val request = InvokeMethodRequest(this.appId, uri.path ?: "/").apply {
            this.httpExtension = extension
        }

        val body = values.bodyValue
        if (body != null) {
            if (values.bodyValue != null) {
                request.setBody(values.bodyValue)
            }
        }
        return request
    }

    override fun supportsRequestAttributes(): Boolean {
        return false
    }

    override fun exchange(requestValues: HttpRequestValues) {
        val request = newRequest(requestValues)
        daprClient.invokeMethod(request, TypeRef.VOID).block()
    }

    override fun exchangeForHeaders(requestValues: HttpRequestValues): HttpHeaders {
        return HttpHeaders.EMPTY
    }

    override fun <T : Any?> exchangeForBody(
        requestValues: HttpRequestValues,
        bodyType: ParameterizedTypeReference<T>?
    ): T {
        val request = newRequest(requestValues)
        if(bodyType != null && bodyType.type != null) {
            return daprClient.invokeMethod(request, TypeRef.get<T>(bodyType.type)).block()
        }else {
            daprClient.invokeMethod(request, TypeRef.VOID).block()
            @Suppress("UNCHECKED_CAST")
            return null as T
        }
    }

    override fun exchangeForBodilessEntity(requestValues: HttpRequestValues): ResponseEntity<Void?> {
        exchange(requestValues)
        return ResponseEntity.ok().build()
    }

    override fun <T : Any?> exchangeForEntity(
        requestValues: HttpRequestValues,
        bodyType: ParameterizedTypeReference<T?>
    ): ResponseEntity<T?> {
        val body = exchangeForBody(requestValues, bodyType)
        return ResponseEntity.ok(body)
    }
}