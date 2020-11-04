package com.labijie.application.payment.callback

import com.fasterxml.jackson.core.type.TypeReference
import com.labijie.application.HttpFormUrlCodec
import com.labijie.application.escapeForRegex
import com.labijie.application.payment.CallbackResponse
import com.labijie.application.payment.ExchangeProtocol
import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.thridparty.wechat.WechatUtilities
import com.labijie.application.web.getBody
import com.labijie.application.web.toPrettyString
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.throwIfNecessary
import io.netty.handler.codec.http.QueryStringDecoder
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.HandlerInterceptor
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class AbstractCallbackMvcInterceptor<TCallbackContext : AbstractCallbackContext, TCallbackHandler>(
    paymentProperties: PaymentProperties,
    providers: List<IPaymentProvider>,
    protected val handlers: List<TCallbackHandler>
) : HandlerInterceptor {
    private val paymentProviders = providers.map { it.name to it }.toMap()


    companion object {
        @JvmStatic
        val log = LoggerFactory.getLogger(PaymentCallbackMvcInterceptor::class.java)


        @JvmStatic
        private fun HttpServletRequest.formatLog(requestBody: ByteArray, message: String): String {
            val builder = StringBuilder()
            builder.appendLine(message)
            builder.appendLine("============ Payment Callback Http Request ============")
            builder.appendLine(this.toPrettyString(requestBody))
            return builder.toString()
        }
    }

    data class InternalContext(
        val requestBody: ByteArray,
        val paymentProvider: IPaymentProvider,
        val version: String,
        val state: String?
    )

    private val pattern: Pattern by lazy {
        val providerNames = paymentProviders.values.joinToString("|") {
            it.name.escapeForRegex()
        }
        val regexString =
            "^/callback/$actionNameInUrl/v(\\w+)/($providerNames)/?(${PaymentProperties.DEFAULT_STATE_RULE_REGEX})\$"
        Pattern.compile(regexString, Pattern.CASE_INSENSITIVE)
    }

    protected abstract val actionNameInUrl: String


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val context = prepareHandlingContext(request)
        if (context != null) {
            try {
                this.doHandling(context, request, response)
            } catch (e: Exception) {
                log.error("Process payment callback fault (provider: ${context.paymentProvider}).", e)
            }
            return false
        }
        return true
    }

    protected fun prepareHandlingContext(request: HttpServletRequest): InternalContext? {
        val body = request.getBody().readBytes() //body 只能读取一次，要小心！
        val uri = request.requestURI
        val matchResult = pattern.matcher(uri)
        if (!matchResult.matches() || matchResult.groupCount() != 3) {
            return null
        }
        val version = matchResult.group(1)
        val providerName = matchResult.group(2)
        val state = matchResult.group(3)
        val provider = this.paymentProviders.get(providerName)
        if (provider == null) {
            log.warn(
                request.formatLog(
                    body,
                    "Ignore payment callback, because no provider named '$providerName' was found."
                )
            )
            return null
        }
        if (!request.method.equals("GET", ignoreCase = true) && !request.method.equals("POST", ignoreCase = true)) {
            log.warn(
                request.formatLog(
                    body,
                    "Ignore payment callback, only http POST or GET method has been supported ( provider: '$providerName') ."
                )
            )
            return null
        }
        val cxt = InternalContext(body, provider, version, state.ifBlank { null })

        if (log.isDebugEnabled) {
            log.debug(request.formatLog(body, "Callback prepared ( provider: $providerName ) ."))
        }
        return cxt
    }

    protected fun HttpServletResponse.writeResponse(
        status: HttpStatus,
        mediaType: MediaType,
        body: String,
        contentLength: Int? = null
    ): Int {
        this.status = status.value()
        this.characterEncoding = Charsets.UTF_8.name()

        this.contentType = MediaType.toString(setOf(mediaType))

        val length = if (body.isEmpty()) 0 else (contentLength ?: body.toByteArray(Charsets.UTF_8).size)
        this.setContentLength(length)
        this.writer.write(body)
        return length
    }

    private fun doHandling(
        context: InternalContext,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {

        val requestContent = parseCallbackPayload(context, request)

        val callBackResponse = processCallbackHandling(context, requestContent, request)

        response.status = HttpStatus.OK.value()
        response.characterEncoding = Charsets.UTF_8.name()
        val length = response.writeResponse(HttpStatus.OK, callBackResponse.mediaType, callBackResponse.body)

        if (log.isDebugEnabled) {
            val logContent = StringBuilder().apply {
                this.appendLine("Callback handled   ( provider: ${context.paymentProvider} ) and response: ")
                this.appendLine("Callback version:  ${context.version}")
                this.appendLine("HttpStatus:  ${response.status}")
                this.appendLine("Content Type:  ${response.contentType}")
                this.appendLine("Content Length:  ${length}")
                this.appendLine("Encoding:  ${response.characterEncoding}")
                this.appendLine("Body:  ${response.characterEncoding}")
                this.appendLine(callBackResponse.body)
            }.toString()

            log.debug(logContent)
        }
    }


    protected fun parseCallbackPayload(context: InternalContext, request: HttpServletRequest): Map<String, String> {
        return if (request.method.equals("get", ignoreCase = true)) {
            QueryStringDecoder(request.queryString, Charsets.UTF_8, false).parameters().map {
                it.key to it.value.joinToString<String?>(",")
            }.toMap()
        } else {
            val charset =
                try {
                    Charset.forName(request.characterEncoding.ifNullOrBlank { Charsets.UTF_8.name() })
                } catch (e: UnsupportedCharsetException) {
                    Charsets.UTF_8
                }


            when (context.paymentProvider.protocol) {
                ExchangeProtocol.Xml -> WechatUtilities.xmlMapper.readValue(
                    context.requestBody,
                    object : TypeReference<Map<String, String>>() {})
                ExchangeProtocol.Json -> JacksonHelper.deserializeMap(context.requestBody, String::class, String::class, true)
                ExchangeProtocol.Form -> HttpFormUrlCodec.decode(context.requestBody, charset).toSingleValueMap()
            }
        }
    }

    protected abstract fun invokeHandler(handler: TCallbackHandler, context: TCallbackContext)

    protected abstract fun makeContext(
        provider: IPaymentProvider,
        requestContent: Map<String, String>,
        request: HttpServletRequest
    ): TCallbackContext

    protected fun processCallbackHandling(
        context: InternalContext,
        requestContent: Map<String, String>,
        request: HttpServletRequest
    ): CallbackResponse {
        val provider = context.paymentProvider

        return if (handlers.isNotEmpty()) {
            try {

                val callbackContext = this.makeContext(provider, requestContent, request)
                callbackContext.version = context.version
                callbackContext.state = context.state
                for (h in handlers) {
//                    it.handleCallback(callbackContext)
                    this.invokeHandler(h, callbackContext)
                    if (callbackContext.handled) {
                        break
                    }
                }
                provider.parseCallbackResponse(true)
            } catch (e: Exception) {
                val error = request.formatLog(context.requestBody,"Payment callback process fault. ( provider: ${provider.name}) .")
                log.error(error, e)
                e.throwIfNecessary()

                provider.parseCallbackResponse(false, "HANDLING_FAULT")
            }
        } else {
            provider.parseCallbackResponse(false, "NONE_HANDLER_FOUND")
        }
    }
}