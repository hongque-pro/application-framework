//package com.labijie.application.configuration
//
//import com.labijie.infra.spring.configuration.getApplicationName
//import com.labijie.infra.spring.configuration.isProduction
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.info.GitProperties
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.core.env.Environment
//import springfox.documentation.builders.ApiInfoBuilder
//import springfox.documentation.builders.PathSelectors
//import springfox.documentation.builders.RequestHandlerSelectors
//import springfox.documentation.spi.DocumentationType
//import springfox.documentation.spring.web.plugins.Docket
//
///**
// *
// * @Author: Anders Xiao
// * @Date: 2021/12/14
// * @Description:
// */
////@EnableOpenApi
//@Configuration(proxyBeanMethods = false)
//class SwaggerAutoConfiguration {
//
//    @Autowired(required = false)
//    private var gitProperties: GitProperties? = null
//
//    @Bean
//    fun allDocket(environment: Environment): Docket {
//
//        val consumers = setOf("application/json")
//
//        val applicationName = environment.getApplicationName(false)
//
//        val info = ApiInfoBuilder()
//            .title("$applicationName API")
//            .version("1.0")
//            .build()
//
//        return Docket(DocumentationType.OAS_30)
//            .enable(!environment.isProduction)
//            .groupName("All")
//            .consumes(consumers)
//            .apiInfo(info)
//            .select()
//            .apis(RequestHandlerSelectors.any())
//            .paths(PathSelectors.any())
//            .build()
//    }
//
//    @Bean
//    fun oauthDocket(environment: Environment): Docket {
//
//        val consumers = setOf("application/json")
//        val info = ApiInfoBuilder()
//            .title("Security OAuth2")
//            .version("1.0")
//            .build()
//
//        return Docket(DocumentationType.OAS_30)
//            .enable(!environment.isProduction)
//            .groupName("OAuth2")
//            .consumes(consumers)
//            .apiInfo(info)
//            .select()
//            .apis(RequestHandlerSelectors.any())
//            .paths(PathSelectors.ant("/oauth/**"))
//            .build()
//    }
//
//    @Bean
//    fun afDocket(environment: Environment): Docket {
//
//        val consumers = setOf("application/json")
//
//        val info = ApiInfoBuilder()
//            .title("Application Framework")
//            .version(gitProperties?.get("build.version") ?: "0.0")
//            .build()
//
//        return Docket(DocumentationType.OAS_30)
//            .enable(!environment.isProduction)
//            .groupName("Framework")
//            .consumes(consumers)
//            .apiInfo(info)
//            .select()
//            .apis(RequestHandlerSelectors.basePackage("com.labijie.application"))
//            .paths(PathSelectors.any())
//            .build()
//    }
//}