package com.labijie.application.open.internal

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.support.HttpRequestWrapper
import java.io.IOException
import java.io.OutputStream
import java.net.URI

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
class ReplaceUriClientHttpRequest constructor(private val uri: URI, request: HttpRequest) :
    HttpRequestWrapper(request), ClientHttpRequest {
    override fun getURI(): URI {
        return uri
    }

    @Throws(IOException::class)
    override fun getBody(): OutputStream {
        return request.body
    }

    @Throws(IOException::class)
    override fun execute(): ClientHttpResponse {
        return request.execute()
    }

    override fun getRequest(): ClientHttpRequest {
        return super.getRequest() as ClientHttpRequest
    }
}