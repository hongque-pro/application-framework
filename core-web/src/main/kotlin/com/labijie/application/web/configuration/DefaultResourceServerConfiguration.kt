package com.labijie.application.web.configuration

import com.labijie.application.web.IResourceAuthorizationConfigurer
import com.labijie.application.web.ResourceAuthorizationRegistry
import com.labijie.infra.spring.configuration.isDevelopment
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@Configuration
class DefaultResourceServerConfiguration(
    private val configurerProvider: ObjectProvider<IResourceAuthorizationConfigurer>,
    private val environment: Environment
) : ResourceServerConfigurerAdapter(), Ordered {
    override fun getOrder(): Int  = -1
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId("server-all")
    }

    override fun configure(http: HttpSecurity) {
        val settings =  http.authorizeRequests()

        val isProduction = environment.activeProfiles.contains("prod") || environment.activeProfiles.contains("production")

        val paths = mutableListOf("/actuator/**",
            "/aliyun/cnf",
            "/aliyun/afs/verify")

        if(!isProduction){
            val swaggerPaths = listOf(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger-resources/configuration/ui")

            paths.addAll(swaggerPaths)

            paths.add("/application-errors")
        }

        settings.antMatchers(*paths.toTypedArray()).permitAll()


        configurerProvider.orderedStream().forEach {
            val r = ResourceAuthorizationRegistry(settings)
            it.configure(r)
        }
    }



}
