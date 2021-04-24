package com.labijie.application.dummy.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
@Configuration
class AppResourceConfiguration : WebMvcConfigurer, IResourceAuthorizationConfigurer {
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        super.configureContentNegotiation(configurer)
        //configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    override fun configure(registry: ResourceAuthorizationRegistry) {
        registry.antMatchers("/test/**").permitAll()
    }
}