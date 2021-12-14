package com.labijie.application.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.web.WrappedResponseBodyAdvice
import com.labijie.application.web.controller.ErrorDescriptionController
import com.labijie.application.web.converter.EnhanceStringToEnumConverterFactory
import com.labijie.application.web.handler.ControllerExceptionHandler
import com.labijie.application.web.interceptor.HttpCacheInterceptor
import com.labijie.application.web.interceptor.HumanVerifyInterceptor
import com.labijie.application.web.interceptor.PrincipalArgumentResolver
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.spring.configuration.isProduction
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.HibernateValidatorConfiguration
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.format.FormatterRegistry
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.*
import javax.validation.Validation
import javax.validation.Validator


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@EnableWebMvc
@Configuration(proxyBeanMethods = false)
@Import(DefaultResourceSecurityConfiguration::class, ErrorDescriptionController::class, SpringDocAutoConfiguration::class)
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
            .maxAge(3600)
            .allowedHeaders("*");
    }


    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(EnhanceStringToEnumConverterFactory())
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(PrincipalArgumentResolver())
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val index = converters.indexOfFirst {
            it is MappingJackson2HttpMessageConverter
        }
        if(index >= 0) {
            converters.removeAt(index)
            converters.add(index, MappingJackson2HttpMessageConverter(JacksonHelper.webCompatibilityMapper))
        }else {
            converters.add(0, MappingJackson2HttpMessageConverter(JacksonHelper.webCompatibilityMapper))
            super.configureMessageConverters(converters)
        }
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
            registry.addRedirectViewController("/swagger", "/swagger-ui.html")
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