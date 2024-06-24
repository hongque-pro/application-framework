/**
 * @author Anders Xiao
 * @date 2024-06-24
 */
package com.labijie.application.component

import com.labijie.application.web.handler.ErrorResponse
import org.springframework.core.Ordered


interface IWebErrorTranslator: Ordered {
    fun isSupported(e: Throwable): Boolean
    fun translate(e: Throwable): ErrorResponse

    override fun getOrder(): Int {
        return 0
    }
}