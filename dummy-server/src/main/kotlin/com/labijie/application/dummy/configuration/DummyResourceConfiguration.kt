package com.labijie.application.dummy.configuration

import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.stereotype.Component

@Component
class DummyResourceConfiguration : IResourceAuthorizationConfigurer {
    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {
        registry.mvcMatchers("/**").permitAll()
    }
}