/**
 * @author Anders Xiao
 * @date 2025/6/21
 */

package com.labijie.application.okhttp

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import java.io.InputStream

typealias OkHttpResponse = okhttp3.Response
typealias OkHttpHeaders = okhttp3.Headers


class OkHttpClientResponse(
    private val okHttpResponse: OkHttpResponse,
) : ClientHttpResponse {

    private var springHeaders: HttpHeaders? = null

    override fun getStatusCode(): HttpStatusCode {
        return HttpStatusCode.valueOf(okHttpResponse.code)
    }

    override fun getStatusText(): String {
        return okHttpResponse.message
    }

    override fun close() {
        val body = okHttpResponse.body
        body?.close()
    }

    override fun getBody(): InputStream {
        val body = okHttpResponse.body
        return body?.byteStream() ?: InputStream.nullInputStream()
    }

    override fun getHeaders(): HttpHeaders {
        if (springHeaders == null) {
            springHeaders = convertHeaders(okHttpResponse.headers)
        }

        return springHeaders!!
    }

    companion object {
        /**
         * Converts the given [OkHttp Headers][okhttp3.Headers] to [Spring Web HttpHeaders][HttpHeaders]
         */
        fun convertHeaders(okHttpHeaders: OkHttpHeaders): HttpHeaders {
            val springHeaders = HttpHeaders()

            for (header in okHttpHeaders) {
                springHeaders.add(header.first, header.second)
            }

            return springHeaders
        }
    }
}