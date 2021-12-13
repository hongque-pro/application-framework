//package com.labijie.application.configuration
//
//import com.labijie.infra.spring.configuration.getApplicationName
//import com.labijie.infra.utils.ifNullOrBlank
//import io.swagger.v3.oas.models.OpenAPI
//import io.swagger.v3.oas.models.info.Info
//import io.swagger.v3.oas.models.info.License
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.core.env.Environment
//
//
///**
// *
// * @Author: Anders Xiao
// * @Date: 2021/12/14
// * @Description:
// */
//@Configuration(proxyBeanMethods = false)
//class SwaggerAutoConfiguration {
//
//    @Bean
//    fun customOpenAPI(env: Environment): OpenAPI {
//        return OpenAPI()
//            .openapi("3.0.0")
//            .info(
//                Info().title("${env.getApplicationName()} API").version(env.getDocVersion()).description(
//                    "Application API Document"
//                )
//                    .termsOfService("http://swagger.io/terms/")
//                    .license(License().name("Apache 2.0"))
//            )
//    }
//
//    private fun Environment.getDocVersion(): String {
//        return this.getProperty("springdoc.version") ?: "Release"
//    }
//}