package com.labijie.application.okhttp

import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.throwIfNecessary
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import okio.buffer
import okio.sink
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


class OkHttpLoggingInterceptor(private val forceTrace: Boolean = false) : Interceptor {
    companion object {
        @JvmStatic
        val log = LoggerFactory.getLogger(OkHttpLoggingInterceptor::class.java)

        @JvmStatic
        fun RequestBody.toBodyString(): String {
            if (this.isDuplex()) {
                return "<duplex content>"
            } else {
                ByteArrayOutputStream().use {
                    it.sink().buffer().use { buffer ->
                        this.writeTo(buffer)
                    }
                    val data = it.toByteArray()
                    return when {
                        this.contentType().isXml -> formatXml(data)
                        this.contentType().isJson -> formatJson(data)
                        this.contentType().isText -> data.toString(Charsets.UTF_8)
                        else -> "<Unknown media type content>"
                    }
                }
            }
        }

        private fun formatJson(bytes: ByteArray) : String {
            return try {
                val node = JacksonHelper.defaultObjectMapper.readTree(bytes)
                JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node)
            }catch (e: Exception) {
                e.throwIfNecessary()
                bytes.toString(Charsets.UTF_8)
            }
        }

        private fun formatXml(bytes: ByteArray): String {
            return try {
                ByteArrayInputStream(bytes).use {
                    bytesStream->
                    val xmlInput = StreamSource(bytesStream)
                    StringWriter().use {
                        stringWriter->
                        val xmlOutput = StreamResult(stringWriter)
                        val transformerFactory = TransformerFactory.newInstance()
                        transformerFactory.setAttribute("indent-number", 2)
                        val transformer = transformerFactory.newTransformer()
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                        transformer.transform(xmlInput, xmlOutput)
                        return xmlOutput.writer.toString()
                    }
                }
            } catch (e: Exception) {
                e.throwIfNecessary()
                bytes.toString(Charsets.UTF_8)
            }
        }

        @JvmStatic
        private val MediaType?.isText: Boolean
            get() {
                val contentType =this?.toString()
                return (!contentType.isNullOrBlank() && contentType.startsWith("text/", ignoreCase = true))
            }

        @JvmStatic
        private val MediaType?.isXml: Boolean
        get() {
            val contentType =this?.toString()
            return (!contentType.isNullOrBlank() && contentType.startsWith("application/xml", ignoreCase = true) ||
                    contentType.equals("application/xml", ignoreCase = true) ||
                    contentType.equals("text/xml", ignoreCase = true))
        }

        @JvmStatic
        private val MediaType?.isJson: Boolean
            get() {
                val contentType = this?.toString()
                return (!contentType.isNullOrBlank() && contentType.startsWith("application/json", ignoreCase = true))
            }

        @JvmStatic
        private fun MediaType?.couldBeRead(): Boolean {
            val str = this?.toString()
            return (str != null && (
                    str.startsWith("text", ignoreCase = true)
                            || str.startsWith("application/json", ignoreCase = true)
                            || str.startsWith("application/xml", ignoreCase = true)
                            || str.startsWith("application/xhtml", ignoreCase = true)
                            || str.startsWith("application/atom", ignoreCase = true)
                            || str.startsWith("application/x-www-form-urlencoded", ignoreCase = true)
                    ))
        }

        private fun Response.readBodyAsString(): String {
            val responseBody = this.body
            if(responseBody != null) {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer

                if ("gzip".equals(this.headers["Content-Encoding"], ignoreCase = true)) {
                    //val gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                if(responseBody.contentLength() > 0L || responseBody.contentLength() < 0) {
                    val charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
                    return if (contentType.couldBeRead()) {
                        val bytes = buffer.clone().readByteArray()
                        when {
                            contentType.isXml -> formatXml(bytes)
                            contentType.isJson -> formatJson(bytes)
                            else -> bytes.toString(charset)
                        }
                    }else{
                        "<unreadable content>"
                    }
                }
            }
            return "<null>"
        }
    }




    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body

        if (log.isDebugEnabled) {
            val stringBuilder = StringBuilder()
            stringBuilder.appendLine()
            stringBuilder.appendLine("============== Http Invocation ==============")
            stringBuilder.appendLine("---------------- Request ----------------")
            stringBuilder.appendLine("OkHttp Settings:")
            stringBuilder.appendLine("   Connect Timeout: ${chain.connectTimeoutMillis()}")
            stringBuilder.appendLine("   Read Timeout: ${chain.readTimeoutMillis() / 1000}s")
            stringBuilder.appendLine("   Write Timeout: ${chain.writeTimeoutMillis() / 1000}ms")
            stringBuilder.appendLine("URL: ${request.url}")
            stringBuilder.appendLine("Method: ${request.method}")
            stringBuilder.appendLine("Headers: ")
            request.headers.forEach {
                stringBuilder.appendLine("  ${it.first}: ${it.second}")
            }
            if (body != null && !request.method.equals("get", ignoreCase = true)) {
                stringBuilder.appendLine("Body:")
                stringBuilder.appendLine(body.toBodyString().ifNullOrBlank { "<null>" })
            }

            try {
                val response = chain.proceed(request)

                stringBuilder.appendLine("---------------- Response ----------------")
                stringBuilder.appendLine("Http Status: ${response.code}")
                stringBuilder.appendLine("Redirect: ${response.isRedirect}")
                stringBuilder.appendLine("Version: ${response.protocol}")
                stringBuilder.appendLine("Headers: ")
                response.headers.forEach {
                    stringBuilder.appendLine("  ${it.first}: ${it.second}")
                }

                val responseBody = response.body

                stringBuilder.appendLine("Body: ")
                if (responseBody != null && responseBody.contentType().couldBeRead()) {
                    stringBuilder.appendLine(response.readBodyAsString().ifNullOrBlank { "<null>" })
                }else{
                    stringBuilder.appendLine("<unreadable content>")
                }

                return response
            } catch (e: Throwable) {
                stringBuilder.appendLine("---------------- Response ----------------")
                stringBuilder.appendLine("There was an error at the request .")
                throw e
            } finally {
                val message = stringBuilder.toString()
                log.debug(message)
                if(forceTrace){
                    println(message)
                }
            }
        } else {
            return chain.proceed(request)
        }
    }
}