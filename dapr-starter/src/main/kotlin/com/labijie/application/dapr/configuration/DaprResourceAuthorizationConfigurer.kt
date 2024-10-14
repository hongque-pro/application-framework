/**
 * @author Anders Xiao
 * @date 2024-01-23
 */
package com.labijie.application.dapr.configuration

import com.labijie.application.web.antMatchers
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import io.dapr.springboot.DaprController
import org.springframework.context.ApplicationContextAware
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


class DaprResourceAuthorizationConfigurer : IResourceAuthorizationConfigurer, ApplicationContextAware {

    private lateinit var context: org.springframework.context.ApplicationContext
    private fun getPermitAllUrlsFromDaprController(): Set<String> {
        val sets = mutableMapOf<HttpMethod, MutableSet<String>>()
        val requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping::class.java)
        val handlerMethodMap = requestMappingHandlerMapping.handlerMethods
        val urlList = mutableSetOf<String>()
        handlerMethodMap.forEach { (key, value) ->
            if (value.beanType == DaprController::class.java) {
                key.pathPatternsCondition?.patterns?.let { urls ->
                    urls.forEach {
                        urlList.add(it.patternString)
                    }
                }
            }
        }
        return urlList
    }



    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        registry.antMatchers(
            "/dapr/**"
        ).permitAll()
        registry.antMatchers(*getPermitAllUrlsFromDaprController().toTypedArray(), ignoreCase = true).permitAll()
    }

    override fun setApplicationContext(applicationContext: org.springframework.context.ApplicationContext) {
        this.context = applicationContext
    }
}