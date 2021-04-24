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
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.HibernateValidatorConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.format.FormatterRegistry
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.RequestHandlerSelectors
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
class WebAutoConfiguration : WebMvcConfigurer {

    @Autowired(required = false)
    private var humanChecker: IHumanChecker? = null

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(EnhanceStringToEnumConverterFactory())
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(PrincipalArgumentResolver())
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0,MappingJackson2HttpMessageConverter(JacksonHelper.webCompatibilityMapper))
        super.configureMessageConverters(converters)
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        super.configureContentNegotiation(configurer)
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(
            HumanVerifyInterceptor(
                humanChecker ?: NoneHumanChecker()
            )
        )
        registry.addInterceptor(HttpCacheInterceptor)
    }

    @Configuration
    @EnableSwagger2
    class SwaggerAutoConfiguration {

        @Bean
        fun swaggerDocket(environment: Environment): Docket {

            val consumers = setOf( "application/json",
                "application/xml")

            val applicationName = environment.getApplicationName(false)

            val info =ApiInfo(
                "$applicationName API",
                "", "1.0",
                "urn:tos",
                Contact(applicationName, "", ""),
                "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0",
                listOf()
            )

            return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(info)
                .produces(consumers)
                .consumes(consumers)
                .select().apis(RequestHandlerSelectors.any()).build()
        }
    }

    @Configuration
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