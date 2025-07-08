/**
 * @author Anders Xiao
 * @date 2025-06-21
 */
package com.labijie.application.okhttp

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HttpMethod.requiresRequestBody
import okio.Buffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.StreamingHttpOutputMessage
import org.springframework.http.client.AbstractClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import java.io.IOException
import java.io.OutputStream
import java.net.MalformedURLException
import java.net.URI

typealias Body = StreamingHttpOutputMessage.Body


class OkHttpClientRequest(
    private val okHttpClient: OkHttpClient,
    private val uri: URI,
    private val method: HttpMethod
) : AbstractClientHttpRequest(), StreamingHttpOutputMessage {


    private var streamingBody: Body? = null

    private var bufferBody: Buffer? = null


    public override fun getMethod(): HttpMethod {
        return method
    }

    override fun getURI(): URI {
        return uri
    }


    override fun setBody(body: Body) {
        Assert.notNull(body, "body must not be null")
        assertNotExecuted()
        Assert.state(bufferBody == null, "getBody has already been used.")
        this.streamingBody = body
    }

    override fun getBodyInternal(headers: HttpHeaders): OutputStream {
        Assert.state(this.streamingBody == null, "setBody has already been used.")

        if (bufferBody == null) {
            bufferBody = Buffer()
        }

        return bufferBody!!.outputStream()
    }

    @Throws(IOException::class)
    protected override fun executeInternal(headers: HttpHeaders): ClientHttpResponse {
        val okHttpRequest = buildRequest(headers)

        val okHttpResponse: Response = this.okHttpClient.newCall(okHttpRequest).execute()

        return OkHttpClientResponse(okHttpResponse)
    }

    @Throws(MalformedURLException::class)
    private fun buildRequest(headers: HttpHeaders): Request {
        val builder = Request.Builder()

        builder.url(uri.toURL())

        var contentType: MediaType? = null

        val contentTypeHeader: String? = headers.getFirst(HttpHeaders.CONTENT_TYPE)
        if (StringUtils.hasText(contentTypeHeader)) {
            contentType = contentTypeHeader?.toMediaType()
        }

        var body: RequestBody? = null

        val buffer = bufferBody
        if (buffer != null) {
            val bodyData = buffer.readByteArray()
            if (headers.getContentLength() < 0) {
                headers.setContentLength(bodyData.size.toLong())
            }

            body = bodyData.toRequestBody(contentType)
        } else if (streamingBody != null) {
            body = StreamingBodyRequestBody(streamingBody!!, contentType, headers.getContentLength())
        } else if (requiresRequestBody(method.name())) {
            body = ByteArray(0).toRequestBody(contentType)
        }

        builder.method(method.name(), body)

        headers.forEach({ name, values ->
            for (value in values) {
                builder.addHeader(name, value)
            }
        })

        return builder.build()
    }
}