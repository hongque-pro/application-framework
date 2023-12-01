package com.labijie.application.dummy.configuration

import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class DummyResourceConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        registry.requestMatchers("/**").permitAll()
    }
}