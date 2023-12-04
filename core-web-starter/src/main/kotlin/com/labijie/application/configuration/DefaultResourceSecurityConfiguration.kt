package com.labijie.application.configuration

import com.labijie.application.web.antMatchers
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@ConditionalOnWebApplication
@Import(AllowAllCorsAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
class DefaultResourceSecurityConfiguration : IResourceAuthorizationConfigurer {
    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        val authenticatedPaths = mutableListOf(
            "/sms/send-user",
        )

        val permitAllPaths = mutableListOf(
            "/sms",
            "/application-errors",
            "/swagger-ui.html",
            "/swagger",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
        )
        registry.antMatchers(*authenticatedPaths.toTypedArray()).authenticated()
        registry.antMatchers(*permitAllPaths.toTypedArray()).permitAll()
    }

    override fun getOrder(): Int = Int.MIN_VALUE


}
