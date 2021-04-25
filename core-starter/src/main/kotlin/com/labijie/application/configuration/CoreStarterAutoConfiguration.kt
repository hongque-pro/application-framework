package com.labijie.application.configuration

import com.labijie.application.web.controller.CoreServiceController
import com.labijie.application.web.controller.RegionController
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
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
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackageClasses = [CoreServiceController::class, RegionController::class])
class CoreStarterAutoConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {
        registry.antMatchers("/sms/**").permitAll()
    }
}