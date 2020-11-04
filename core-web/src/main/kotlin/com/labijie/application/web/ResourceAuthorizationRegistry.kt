package com.labijie.application.web

import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-14
 */
data class ResourceAuthorizationRegistry(
    private val registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry){

    fun antMatchers(antPatterns:Array<String>, ignoreCase:Boolean = false, method: HttpMethod? = null): ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl {
        val matchers = antPatterns.toList().map {
            AntPathRequestMatcher(it, method?.name, !ignoreCase)
        }.toTypedArray()
        return registry.requestMatchers(*matchers)
    }

    fun antMatchers(vararg antPatterns:String): ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl {
        return registry.antMatchers(*antPatterns)
    }
}