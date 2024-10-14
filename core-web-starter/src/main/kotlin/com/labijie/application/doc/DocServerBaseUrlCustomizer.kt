/**
 * @author Anders Xiao
 * @date 2024-10-14
 */
package com.labijie.application.doc

import com.labijie.application.UrlProtocol
import com.labijie.application.configuration.ApplicationWebProperties
import com.labijie.infra.isProduction
import org.springdoc.core.customizers.ServerBaseUrlCustomizer
import org.springframework.core.env.Environment
import java.net.MalformedURLException
import java.net.URL


class DocServerBaseUrlCustomizer(
    private val environment: Environment,
    private val webProperties: ApplicationWebProperties
) : ServerBaseUrlCustomizer {
    override fun customize(serverBaseUrl: String): String {
        val urlProtocol = webProperties.docServerUrlProtocol
        if (urlProtocol == UrlProtocol.DEFAULT) {
            return serverBaseUrl
        }

        var baseUrl = serverBaseUrl
        try {
            val url = URL(serverBaseUrl)
            baseUrl = URL(getProtocol(url), url.host, url.file).toString()
        } catch (ex: MalformedURLException) {
            // nothing we can do
        }

        return baseUrl
    }

    private fun getProtocol(url: URL): String {
        if (!url.protocol.equals("http", ignoreCase = true)) {
            return url.protocol
        }

        return when (webProperties.docServerUrlProtocol) {
            UrlProtocol.DEFAULT -> url.protocol
            UrlProtocol.AUTO -> (if(environment.isProduction) "https" else "http")
            UrlProtocol.HTTP -> "http"
            UrlProtocol.HTTPS -> "https"
            else-> url.protocol
        }
    }
}