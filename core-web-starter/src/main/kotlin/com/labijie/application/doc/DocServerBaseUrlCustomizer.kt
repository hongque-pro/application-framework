/**
 * @author Anders Xiao
 * @date 2024-10-14
 */
package com.labijie.application.doc

import com.labijie.application.configuration.ApplicationWebProperties
import org.springdoc.core.customizers.ServerBaseUrlCustomizer
import org.springframework.http.HttpRequest


class DocServerBaseUrlCustomizer(
    private val webProperties: ApplicationWebProperties
) : ServerBaseUrlCustomizer {

    override fun customize(
        serverBaseUrl: String,
        request: HttpRequest
    ): String {
        return webProperties.serverBaseUrl.orEmpty()
    }

}