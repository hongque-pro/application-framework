package com.labijie.application.httpclient

import com.labijie.application.configuration.HttpClientProperties
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.throwIfNecessary
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.io.ByteArrayInputStream
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


class HttpClientLoggingInterceptor(
    private val httpClientProperties: HttpClientProperties,
    private val forceTrace: Boolean = false) : ClientHttpRequestInterceptor {
    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(HttpClientLoggingInterceptor::class.java)

        @JvmStatic
        fun ClientHttpResponse.toBodyString(): String {
            val body = this.body.readAllBytes()
            val contentType = this.headers.contentType ?: MediaType.APPLICATION_OCTET_STREAM
            return when {
                contentType.isXml -> formatXml(body)
                contentType.isJson -> formatJson(body)
                contentType.isHtml -> body.toString(Charsets.UTF_8).let { Jsoup.parse(it).toString() }
                contentType.isText -> body.toString(Charsets.UTF_8)
                else->"<unreadable content>"
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
        private val MediaType?.isHtml: Boolean
            get() {
                val contentType =this?.toString()
                return (!contentType.isNullOrBlank() && contentType.startsWith(TEXT_HTML_VALUE, ignoreCase = true))
            }

        @JvmStatic
        private val MediaType?.isXml: Boolean
            get() {
                val contentType =this?.toString()
                return !contentType.isNullOrBlank() && (
                        contentType.startsWith("application/xml", ignoreCase = true) ||
                        contentType.startsWith("application/xhtml") ||
                        contentType.equals("application/xml", ignoreCase = true) ||
                        contentType.equals("text/xml", ignoreCase = true)
                    )
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
    }


    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        if(log.isDebugEnabled) {

            val stringBuilder = StringBuilder()
            stringBuilder.appendLine()
            stringBuilder.appendLine("============== Http Invocation ==============")
            stringBuilder.appendLine("---------------- Request ----------------")
            stringBuilder.appendLine("HTTP Settings:")
            stringBuilder.appendLine("   Connect Timeout: ${httpClientProperties.connectTimeout}")
            stringBuilder.appendLine("   Read Timeout: ${httpClientProperties.readTimeout}")
            stringBuilder.appendLine("   Write Timeout: ${httpClientProperties.writeTimeout}")
            stringBuilder.appendLine("URL: ${request.uri}")
            stringBuilder.appendLine("Method: ${request.method.name()}")
            stringBuilder.appendLine("Headers: ")
            request.headers.forEach {
                stringBuilder.appendLine("  ${it.key}: ${it.value}")
            }
            if (body.isNotEmpty() && !request.method.name().equals("get", ignoreCase = true)) {
                stringBuilder.appendLine("Body:")
                stringBuilder.appendLine(body.toString(Charsets.UTF_8).ifNullOrBlank { "<null>" })
            }

            try {
                val response = BufferedBodyClientHttpResponse(execution.execute(request, body))
                stringBuilder.appendLine("---------------- Response ----------------")
                stringBuilder.appendLine("Http Status: ${response.statusCode.value()}")
                stringBuilder.appendLine("Redirect: ${response.statusCode.is3xxRedirection}")
                stringBuilder.appendLine("ContentLength: ${response.headers.contentLength}")
                stringBuilder.appendLine("Headers: ")
                response.headers.forEach {
                    stringBuilder.appendLine("  ${it.key}: ${it.value}")
                }

                stringBuilder.appendLine("Body: ")
                if (response.headers.contentType.couldBeRead()) {
                    stringBuilder.appendLine(response.toBodyString().ifNullOrBlank { "<null>" })
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
        }
        return execution.execute(request, body)
    }
}