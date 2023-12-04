package com.labijie.application.auth.configuration

import com.labijie.infra.oauth2.TwoFactorPrincipal
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.get
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@Configuration(proxyBeanMethods = false)
class AuthDocConfiguration : InitializingBean {
    override fun afterPropertiesSet() {
        SpringDocUtils.getConfig().apply {
            addRequestWrapperToIgnore(RegisteredClient::class.java)
        }
    }
}