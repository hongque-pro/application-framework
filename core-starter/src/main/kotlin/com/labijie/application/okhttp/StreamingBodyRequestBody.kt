/**
 * @author Anders Xiao
 * @date 2025-06-21
 */
package com.labijie.application.okhttp

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import org.springframework.http.StreamingHttpOutputMessage
import java.io.IOException


internal class StreamingBodyRequestBody(
    private val streamingBody: StreamingHttpOutputMessage.Body,
    private val contentType: MediaType?,
    private val contentLength: Long?
) : RequestBody() {

    override fun contentType(): MediaType? {
        return contentType
    }

    override fun contentLength(): Long {
        return contentLength ?: -1L
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        streamingBody.writeTo(sink.outputStream())
    }

    override fun isOneShot(): Boolean {
        return true
    }
}