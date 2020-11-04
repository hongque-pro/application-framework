package com.labijie.application.open.configuration

import com.labijie.application.open.model.OpenAppStatus
import com.labijie.application.open.service.IOpenAppService
import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.util.UrlPathHelper
import javax.servlet.http.HttpServletRequest

class JdbcCorsConfigurationSource(
    private val apiProperties: OpenApiProperties,
    private val appService: IOpenAppService
) : CorsConfigurationSource {
    private val pathHelper: UrlPathHelper = UrlPathHelper()
    private val pathMatcher: PathMatcher = AntPathMatcher()

    private val defaultAllowAll: CorsConfiguration by lazy {
        CorsConfiguration().applyPermitDefaultValues().also {
            it.maxAge = 1L.coerceAtLeast(this.apiProperties.jsApiCors.maxAge)
        }
    }


    override fun getCorsConfiguration(request: HttpServletRequest): CorsConfiguration? {
        val path = pathHelper.getLookupPathForRequest(request)
        if (pathMatcher.match(apiProperties.jsApiCors.pathPattern, path)) {
            when (apiProperties.jsApiCors.allowedOriginsPolicy) {
                CorsPolicy.ALL -> defaultAllowAll
                CorsPolicy.PARTNER -> {
                    val key = request.getParameter("key")
                    if (!key.isNullOrBlank()) {
                        val app = this.appService.getByJsApiKey(key.trim())

                        if (app != null && app.jsApiDomain.isNotBlank() && app.status == OpenAppStatus.NORMAL) {
                            return CorsConfiguration(this.defaultAllowAll).apply {
                                if (app.jsApiDomain.startsWith("https//") || app.jsApiDomain.startsWith("http://")) {
                                    this.allowedOrigins = listOf(app.jsApiDomain, "http://localhost:5500")
                                } else {
                                    this.allowedOrigins = listOf(
                                        "http://${app.jsApiDomain}",
                                        "https://${app.jsApiDomain}",
                                        "http://localhost:5500"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
    }
}