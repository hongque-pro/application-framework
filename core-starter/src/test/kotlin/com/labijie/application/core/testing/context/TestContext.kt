package com.labijie.application.core.testing.context

import com.labijie.application.configuration.ApplicationCoreAutoConfiguration
import com.labijie.application.configuration.HttpClientAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@Configuration
@ImportAutoConfiguration(HttpClientAutoConfiguration::class, RestTemplateAutoConfiguration::class, ApplicationCoreAutoConfiguration::class)
class TestContext {
}