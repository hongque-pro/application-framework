package com.labijie.application.auth.testing.context

import com.labijie.application.auth.configuration.ApplicationAuthServerAutoConfiguration
import com.labijie.application.auth.configuration.ApplicationAuthServerControllerAutoConfiguration
import com.labijie.caching.configuration.CachingAutoConfiguration
import com.labijie.infra.oauth2.configuration.OAuth2DependenciesAutoConfiguration
import com.labijie.infra.oauth2.configuration.OAuth2SecurityAutoConfiguration
import com.labijie.infra.oauth2.configuration.OAuth2ServerAutoConfiguration
import com.labijie.infra.oauth2.resource.configuration.ResourceServerAutoConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.test.web.servlet.MockMvc

/**
 * @author Anders Xiao
 * @date 2025/7/14
 */
@ImportAutoConfiguration(
    CachingAutoConfiguration::class,
    OAuth2DependenciesAutoConfiguration::class,
    OAuth2ServerAutoConfiguration::class,
    OAuth2SecurityAutoConfiguration::class,
    ResourceServerAutoConfiguration::class,
    ApplicationAuthServerAutoConfiguration::class,
    ApplicationAuthServerControllerAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
class DummyServerAutoConfiguration {

    @Autowired
    private lateinit var mockMvc: MockMvc


    fun testAppleLogin() {

    }
}