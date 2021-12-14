package com.labijie.application.configuration

import com.labijie.infra.spring.configuration.getApplicationName
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment


/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/14
 * @Description:
 */
@Configuration(proxyBeanMethods = false)
class SpringDocAutoConfiguration {

    @Bean
    fun customOpenAPI(env: Environment): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("${env.getApplicationName()} API").version(env.getDocVersion()).description(
                    "Application API Document"
                )
                    .termsOfService("http://swagger.io/terms/")
                    .license(License().name("Apache 2.0"))
            )
    }

    private fun Environment.getDocVersion(): String {
        return this.getProperty("springdoc.version") ?: "Release"
    }
}