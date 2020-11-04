package com.labijie.application.web.configuration

import com.labijie.application.web.IResourceAuthorizationConfigurer
import com.labijie.application.web.ResourceAuthorizationRegistry
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 *
 * @author lishiwen
 * @date 19-12-10
 * @since JDK1.8
 */
@Configuration
@ComponentScan(value = ["com.labijie.application.web.controller", "com.labijie.application.web.service"])
class CoreStarterAutoConfiguration : IResourceAuthorizationConfigurer {

    override fun configure(registry: ResourceAuthorizationRegistry) {
        registry.antMatchers("/sms/**").permitAll()
    }
}