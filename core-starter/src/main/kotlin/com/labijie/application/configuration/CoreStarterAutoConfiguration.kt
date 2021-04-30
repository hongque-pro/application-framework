package com.labijie.application.configuration

import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer

/**
 *
 * @author lishiwen
 * @date 19-12-10
 * @since JDK1.8
 */
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
@ComponentScan(value = ["com.labijie.application.web.controller", "com.labijie.application.service"])
class CoreStarterAutoConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {
        registry.antMatchers("/sms/**").permitAll()
    }
}