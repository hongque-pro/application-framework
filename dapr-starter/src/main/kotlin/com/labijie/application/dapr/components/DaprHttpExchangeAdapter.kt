package com.labijie.application.dapr.components

import com.fasterxml.jackson.core.JsonParseException
import com.labijie.application.ErrorCodedStatusException
import com.labijie.application.ObjectUtils.parseQueryStringDecoded
import com.labijie.application.dapr.components.DaprHttpExchangeAdapter.DaprApiResult.Companion.toException
import com.labijie.application.orDefault
import com.labijie.application.web.handler.ErrorResponse
import com.labijie.infra.json.JacksonHelper
import io.dapr.client.DaprClient
import io.dapr.client.DaprHttp
import io.dapr.client.domain.HttpExtension
import io.dapr.client.domain.InvokeMethodRequest
import io.dapr.exceptions.DaprException
import io.dapr.utils.TypeRef
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.service.invoker.HttpExchangeAdapter
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.util.UriTemplate
import java.io.IOException
import java.lang.reflect.Type
import java.util.function.Consumer


/**
 * @author Anders Xiao
 * @date 2025/9/9
 */
class DaprHttpExchangeAdapter(
    private val daprClient: DaprClient,
    private val appId: String,
    private val useGrpc: Boolean = false
) : HttpExchangeAdapter {

    companion object {
        @JvmStatic
        fun create(daprClient: DaprClient, appId: String): DaprHttpExchangeAdapter {
            return DaprHttpExchangeAdapter(daprClient, appId)
        }
    }



    class DaprApiResult<T>(private val daprAppId: String) {
        private var error: ErrorResponse? = null
        var data: T? = null
            private set

        fun isError(): Boolean {
            return error != null
        }

        fun getError(): ErrorResponse? {
            return error
        }

        fun getResultOrError(httpStatus: Int? = null): T {
            error?.let {
                err->
                throw err.toException(daprAppId, httpStatus)
            }
            return data!!
        }

        companion object {
            fun ErrorResponse.toException(daprAppId: String, httpStatus: Int? = null): ErrorCodedStatusException {
                val err = this
                return ErrorCodedStatusException(err.error, err.errorDescription, status = httpStatus?.let { HttpStatus.valueOf(it) }).apply {
                    err.details?.forEach {
                        if(it.value is String) {
                            args.putIfAbsent(it.key, it.value as String)
                        }
                    }
                    if(daprAppId.isNotBlank()) {
                        args["dapr_app_id"] = daprAppId
                    }
                }
            }

            @Throws(Exception::class)
            fun <T> fromJson(daprAppId: String, byte: ByteArray, type: Type): DaprApiResult<T> {

                val root = JacksonHelper.webCompatibilityMapper.readTree(byte)
                val result = DaprApiResult<T>(daprAppId)
                if (root.has("error")) {
                    result.error = JacksonHelper.webCompatibilityMapper.treeToValue(root, ErrorResponse::class.java)
                } else {
                    val tf  = JacksonHelper.webCompatibilityMapper.typeFactory
                    val javaType = tf.constructType(type)
                    result.data = JacksonHelper.webCompatibilityMapper.treeToValue<T>(root, javaType)
                }
                return result
            }

            @Throws(Exception::class)
            fun fromError(byte: ByteArray): ErrorResponse? {
                try {
                    val root = JacksonHelper.webCompatibilityMapper.readTree(byte)
                    if (root.has("error")) {
                        return JacksonHelper.webCompatibilityMapper.treeToValue(root, ErrorResponse::class.java)
                    }
                    return null
                }catch (_: IOException) {
                    return null
                }
                catch (_: JsonParseException) {
                    return null
                }
            }
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

    private fun <T : Any?> invokeHttp(
        requestValues: HttpRequestValues,
        bodyType: ParameterizedTypeReference<T>?
    ): T? {
        try {
            val request = newRequest(requestValues)
            if (bodyType != null && bodyType.type != null) {
                val bytes = daprClient.invokeMethod(request, TypeRef.BYTE_ARRAY).block()
                val result = DaprApiResult.fromJson<T>(
                    this.appId,
                    bytes.orDefault(),
                    bodyType.type,
                )
                return result.getResultOrError()
            } else {
                daprClient.invokeMethod(request, TypeRef.VOID).block()
                @Suppress("UNCHECKED_CAST")
                return null
            }
        }catch (e: DaprException) {
            val msg = e.payload // 包含 body
            msg?.let {
                val err = DaprApiResult.fromError(it)
                err?.let { throw err.toException(this.appId, e.httpStatusCode) }
            }
            throw e
        }
    }


    override fun supportsRequestAttributes(): Boolean {
        return false
    }

    override fun exchange(requestValues: HttpRequestValues) {
        invokeHttp<Void>(requestValues, null)
    }


    override fun exchangeForHeaders(requestValues: HttpRequestValues): HttpHeaders {
        return HttpHeaders.EMPTY
    }

    override fun <T : Any?> exchangeForBody(
        requestValues: HttpRequestValues,
        bodyType: ParameterizedTypeReference<T>?
    ): T? {
        return invokeHttp(requestValues, bodyType)
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