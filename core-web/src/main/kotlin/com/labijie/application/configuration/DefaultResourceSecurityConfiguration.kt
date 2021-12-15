package com.labijie.application.configuration

import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
class DefaultResourceSecurityConfiguration : IResourceAuthorizationConfigurer {
    override fun getOrder(): Int = Int.MIN_VALUE

    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {
        //TODO: wait resource server starter fix this bug
        registry.and().cors()
        registry.antMatchers(HttpMethod.OPTIONS).permitAll()

        val paths = mutableListOf(
            "/actuator/**",
            "/aliyun/cnf",
            "/aliyun/afs/verify"
        )

        val swaggerPaths = listOf(
            "/swagger-ui.html",
            "/swagger",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
//                "/configuration/ui",
            "/swagger-resources/**",
//                "/configuration/security",
            "/swagger-ui/**",
//                "/webjars/**",
//                "/swagger-resources/configuration/ui"
        )

        paths.addAll(swaggerPaths)

        paths.add("/application-errors")

        registry.mvcMatchers(*paths.toTypedArray()).permitAll()
    }


}
