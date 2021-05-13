package com.labijie.application.web.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.web.controller.ErrorDescriptionController
import com.labijie.application.web.WrappedResponseBodyAdvice
import com.labijie.application.web.converter.EnhanceStringToEnumConverterFactory
import com.labijie.application.web.handler.ControllerExceptionHandler
import com.labijie.application.web.interceptor.HttpCacheInterceptor
import com.labijie.application.web.interceptor.HumanVerifyInterceptor
import com.labijie.application.web.interceptor.PrincipalArgumentResolver
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.spring.configuration.getApplicationName
import com.labijie.infra.spring.configuration.isProduction
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.HibernateValidatorConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.format.FormatterRegistry
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.*
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.oas.annotations.EnableOpenApi
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import javax.validation.Validation
import javax.validation.Validator


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@Configuration(proxyBeanMethods = false)
@Import(DefaultResourceSecurityConfiguration::class, ErrorDescriptionController::class)
@AutoConfigureAfter(Environment::class)
@ConditionalOnWebApplication
@Order(1000)
class WebAutoConfiguration : WebMvcConfigurer {

    @Autowired(required = false)
    private var humanChecker: IHumanChecker? = null

    @Autowired
    private lateinit var environment:Environment

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .allowCredentials(true)
            .maxAge(3600)
            .allowedHeaders("*");
    }


    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(EnhanceStringToEnumConverterFactory())
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(PrincipalArgumentResolver())
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0, MappingJackson2HttpMessageConverter(JacksonHelper.webCompatibilityMapper))
        super.configureMessageConverters(converters)
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        super.configureContentNegotiation(configurer)
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(
                HumanVerifyInterceptor(
                        humanChecker ?: NoneHumanChecker()
                )
        )
        registry.addInterceptor(HttpCacheInterceptor)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        super.addViewControllers(registry)
        if(!environment.isProduction) {
            registry.addRedirectViewController("/swagger", "/swagger-ui/index.html")
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableOpenApi
    class SwaggerAutoConfiguration {

        @Autowired(required = false)
        private var gitProperties: GitProperties? = null

        @Bean
        fun allDocket(environment: Environment): Docket {

            val consumers = setOf("application/json")

            val applicationName = environment.getApplicationName(false)

            val info = ApiInfoBuilder()
                    .title("$applicationName API")
                    .version("1.0")
                    .build()

            return Docket(DocumentationType.OAS_30)
                    .enable(!environment.isProduction)
                    .groupName("All")
                    .consumes(consumers)
                    .apiInfo(info)
                    .select()
                    .apis(RequestHandlerSelectors.any())
                    .paths(PathSelectors.any())
                    .build()
        }

        @Bean
        fun oauthDocket(environment: Environment): Docket {

            val consumers = setOf("application/json")

            val applicationName = environment.getApplicationName(false)

            val info = ApiInfoBuilder()
                    .title("Security OAuth2")
                    .version("1.0")
                    .build()

            return Docket(DocumentationType.OAS_30)
                    .enable(!environment.isProduction)
                    .groupName("OAuth2")
                    .consumes(consumers)
                    .apiInfo(info)
                    .select()
                    .apis(RequestHandlerSelectors.any())
                    .paths(PathSelectors.ant("/oauth/**"))
                    .build()
        }

        @Bean
        fun afDocket(environment: Environment): Docket {

            val consumers = setOf("application/json")

            val info = ApiInfoBuilder()
                    .title("Application Framework")
                    .version(gitProperties?.get("build.version") ?: "0.0")
                    .build()

            return Docket(DocumentationType.OAS_30)
                    .enable(!environment.isProduction)
                    .groupName("Framework")
                    .consumes(consumers)
                    .apiInfo(info)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.labijie.application"))
                    .paths(PathSelectors.any())
                    .build()
        }
    }

    @Configuration(proxyBeanMethods = false)
    class HibernateValidationAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(Validator::class)
        fun validator(): Validator {
            val validatorFactory = Validation.byProvider(HibernateValidator::class.java)
                    .configure()
                    .addProperty(HibernateValidatorConfiguration.FAIL_FAST, "true")
                    .buildValidatorFactory()
            return validatorFactory.validator
        }
    }


    @Bean
    @ConditionalOnMissingBean(MethodValidationPostProcessor::class)
    fun methodValidationPostProcessor(validator: Validator): MethodValidationPostProcessor {
        val postProcessor = MethodValidationPostProcessor()
        postProcessor.setValidator(validator);
        return postProcessor
    }

    @Bean
    fun controllerExceptionHandler(): ControllerExceptionHandler {
        return ControllerExceptionHandler()
    }


    @Bean
    fun wrappedResponseBodyAdvice() = WrappedResponseBodyAdvice()
}