package com.labijie.application.web.configuration

import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import com.labijie.infra.spring.configuration.isProduction
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@Configuration(proxyBeanMethods = false)
class DefaultResourceSecurityConfiguration : IResourceAuthorizationConfigurer, Ordered {
    override fun getOrder(): Int  = -1


    override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {

        val paths = mutableListOf("/actuator/**",
                "/aliyun/cnf",
                "/aliyun/afs/verify")

        paths.add("/swagger-ui/**")

        paths.add("/application-errors")

        registry.antMatchers(*paths.toTypedArray()).permitAll()
    }


}
