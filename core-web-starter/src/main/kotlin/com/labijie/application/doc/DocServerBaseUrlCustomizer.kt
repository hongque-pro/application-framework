/**
 * @author Anders Xiao
 * @date 2024-10-14
 */
package com.labijie.application.doc

import com.labijie.application.UrlProtocol
import com.labijie.application.configuration.ApplicationWebProperties
import com.labijie.application.getOriginProtocol
import com.labijie.infra.isProduction
import org.apache.hc.core5.net.URIBuilder
import org.springdoc.core.customizers.ServerBaseUrlCustomizer
import org.springframework.core.env.Environment
import org.springframework.http.HttpRequest
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.MalformedURLException
import java.net.URI
import java.net.URL


class DocServerBaseUrlCustomizer(
    private val environment: Environment,
    private val webProperties: ApplicationWebProperties
) : ServerBaseUrlCustomizer {
    override fun customize(
        serverBaseUrl: String,
        request: HttpRequest
    ): String {
        val urlProtocol = webProperties.docServerUrlProtocol
        if (urlProtocol == UrlProtocol.DEFAULT) {
            return serverBaseUrl
        }

        var baseUrl = serverBaseUrl
        try {
            baseUrl = UriComponentsBuilder
                .fromUriString(serverBaseUrl)
                .scheme(getProtocol(request, serverBaseUrl))
                .toUriString()

        } catch (ex: MalformedURLException) {
            // nothing we can do
        }

        return baseUrl
    }

    private fun getProtocol(request: HttpRequest, url: String): String {
        val uri = URI(url)
        if (!uri.scheme.equals("http", ignoreCase = true)) {
            return uri.scheme
        }

        return when (webProperties.docServerUrlProtocol) {
            UrlProtocol.DEFAULT -> uri.scheme
            UrlProtocol.AUTO -> request.getOriginProtocol()  ?: (if (environment.isProduction) "https" else "http")
            UrlProtocol.HTTP -> "http"
            UrlProtocol.HTTPS -> "https"
        }
    }

}