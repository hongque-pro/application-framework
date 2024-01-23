/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.dapr.configuration

import com.labijie.application.web.antMatchers
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer


class DaprResourceAuthorizationConfigurer : IResourceAuthorizationConfigurer {
    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        registry.antMatchers(
            "/actors/**",
            "/healthz",
            "/dapr/**"
        ).permitAll()
    }
}