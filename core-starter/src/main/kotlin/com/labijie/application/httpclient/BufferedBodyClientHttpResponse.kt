package com.labijie.application.httpclient

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

/**
 * @author Anders Xiao
 * @date 2023-12-03
 */
internal class BufferedBodyClientHttpResponse(response: ClientHttpResponse) : ClientHttpResponse {
    private val response: ClientHttpResponse
    private val headers: HttpHeaders
    private val statusCode: HttpStatusCode
    private val bufferedInputStream: ByteArrayInputStream

    init {
        this.response = response
        this.headers = response.headers
        this.statusCode = response.statusCode
        val bytes = response.body.readAllBytes()
        bufferedInputStream = bytes.inputStream()
        response.close()
    }


    override fun getHeaders(): HttpHeaders {
        return response.headers
    }

    @Throws(IOException::class)
    override fun getBody(): InputStream {
        bufferedInputStream.reset()
        return bufferedInputStream
    }


    @Throws(IOException::class)
    override fun getStatusCode(): HttpStatusCode {
        return response.statusCode
    }

    @Deprecated("Deprecated in Spring 6.0")
    @Throws(IOException::class)
    override fun getRawStatusCode(): Int {
        return response.statusCode.value()
    }

    @Throws(IOException::class)
    override fun getStatusText(): String {
        return response.statusText
    }

    override fun close() {
        bufferedInputStream.close()
    }
}