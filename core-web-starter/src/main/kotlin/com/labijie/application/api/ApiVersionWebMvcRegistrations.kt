/**
 * @author Anders Xiao
 * @date 2025/9/18
 */
package com.labijie.application.api

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


class ApiVersionWebMvcRegistrations : WebMvcRegistrations {
    override fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping {
        return VersionedRequestMappingHandlerMapping()
    }
}