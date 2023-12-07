package com.labijie.application.web.client

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.lang.Nullable
import java.io.IOException
import java.io.InputStream
import java.io.PushbackInputStream

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
internal class MessageBodyClientHttpResponseWrapper(response: ClientHttpResponse) :
    ClientHttpResponse {
    private val response: ClientHttpResponse

    @Nullable
    private var pushbackInputStream: PushbackInputStream? = null

    init {
        this.response = response
    }

    fun pushBack() {

    }

    /**
     * Indicates whether the response has a message body.
     *
     * Implementation returns `false` for:
     *
     *  * a response status of `1XX`, `204` or `304`
     *  * a `Content-Length` header of `0`
     *
     * @return `true` if the response has a message body, `false` otherwise
     * @throws IOException in case of I/O errors
     */
    @Throws(IOException::class)
    fun hasMessageBody(): Boolean {
        val status = HttpStatus.resolve(statusCode.value())
        if (status != null && (status.is1xxInformational || status == HttpStatus.NO_CONTENT || status == HttpStatus.NOT_MODIFIED)) {
            return false
        }
        return headers.contentLength != 0L
    }

    /**
     * Indicates whether the response has an empty message body.
     *
     * Implementation tries to read the first bytes of the response stream:
     *
     *  * if no bytes are available, the message body is empty
     *  * otherwise it is not empty and the stream is reset to its start for further reading
     *
     * @return `true` if the response has a zero-length message body, `false` otherwise
     * @throws IOException in case of I/O errors
     */
    @Throws(IOException::class)
    fun hasEmptyMessageBody(): Boolean {
        val body = response.body ?: return true
        // Per contract body shouldn't be null, but check anyway..
        return if (body.markSupported()) {
            body.mark(1)
            if (body.read() == -1) {
                true
            } else {
                body.reset()
                false
            }
        } else {
            pushbackInputStream = PushbackInputStream(body)
            val b = pushbackInputStream!!.read()
            if (b == -1) {
                true
            } else {
                pushbackInputStream!!.unread(b)
                false
            }
        }
    }

    override fun getHeaders(): HttpHeaders {
        return response.headers
    }

    @Throws(IOException::class)
    override fun getBody(): InputStream {
        return pushbackInputStream ?: response.body
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
        response.close()
    }
}