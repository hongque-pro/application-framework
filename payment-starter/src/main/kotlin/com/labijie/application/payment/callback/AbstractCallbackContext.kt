package com.labijie.application.payment.callback

import javax.servlet.http.HttpServletRequest

abstract class AbstractCallbackContext(
    val paymentProvider: String,
    val httpRequest: HttpServletRequest
) {
    /**
     * 指示是否已经处理过，如果有多个 CallbackHandler ，如果为 true,  将不再通过下一个 CallbackHandler 处理。
     * 当处理失败时，该值忽略（其中任意一个 handler 失败，认为失败）。
     */
    var handled: Boolean = false

    var version: String = ""
    internal set

    var state: String? = null
    internal set
}