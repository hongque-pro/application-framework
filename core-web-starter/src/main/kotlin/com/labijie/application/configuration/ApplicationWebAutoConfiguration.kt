package com.labijie.application.configuration

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.web.controller.CaptchaController
import com.labijie.application.web.controller.SmsController
import com.labijie.application.service.CaptchaHumanChecker
import com.labijie.application.web.WrappedResponseBodyAdvice
import com.labijie.application.web.controller.ApplicationController
import com.labijie.application.web.controller.FileController
import com.labijie.application.web.converter.EnhanceStringToEnumConverterFactory
import com.labijie.application.web.handler.ControllerExceptionHandler
import com.labijie.application.web.interceptor.HttpCacheInterceptor
import com.labijie.application.web.interceptor.HumanVerifyInterceptor
import com.labijie.application.web.interceptor.PrincipalArgumentResolver
import com.labijie.infra.isProduction
import com.labijie.infra.json.JacksonHelper
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.HibernateValidatorConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.MessageSource
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
import org.springframework.web.servlet.config.annotation.*


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@EnableWebMvc
@Configuration(proxyBeanMethods = false)
@Import(DefaultResourceSecurityConfiguration::class, SpringDocAutoConfiguration::class)
@AutoConfigureAfter(Environment::class)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
@ConditionalOnWebApplication
class ApplicationWebAutoConfiguration : WebMvcConfigurer {

    @Autowired(required = false)
    private var humanChecker: IHumanChecker? = null

    @Autowired
    private lateinit var environment: Environment

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .maxAge(3600)
            .allowedHeaders("*")
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
        if (index >= 0) {
            converters.removeAt(index)
            converters.add(index, MappingJackson2HttpMessageConverter(JacksonHelper.webCompatibilityMapper))
        } else {
            converters.add(converters.size, MappingJackson2HttpMessageConverter(JacksonHelper.webCompatibilityMapper))
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
        if (!environment.isProduction) {
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
    fun controllerExceptionHandler(messageSource: MessageSource): ControllerExceptionHandler {
        return ControllerExceptionHandler(messageSource)
    }


    @Bean
    fun wrappedResponseBodyAdvice() = WrappedResponseBodyAdvice()

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(IHumanChecker::class)
    class CaptchaAutoConfiguration {
        @Bean
        fun captchaHumanChecker(applicationProperties: ApplicationCoreProperties): CaptchaHumanChecker {
            return CaptchaHumanChecker(applicationProperties)
        }

        @Bean
        @ConditionalOnProperty(prefix = "application.web.service-controllers", name = ["enabled"], havingValue = "true", matchIfMissing = true)
        fun captchaController(applicationProperties: ApplicationCoreProperties, captchaHumanChecker: CaptchaHumanChecker): CaptchaController {
            return CaptchaController(applicationProperties, captchaHumanChecker)
        }
    }
}