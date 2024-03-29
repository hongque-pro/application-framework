package com.labijie.application.payment.configuration

import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.callback.IPaymentCallbackHandler
import com.labijie.application.payment.callback.IRefundCallbackHandler
import com.labijie.application.payment.callback.PaymentCallbackMvcInterceptor
import com.labijie.application.payment.callback.RefundCallbackMvcInterceptor
import com.labijie.application.web.antMatchers
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.util.AntPathMatcher
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.stream.Collectors

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class PaymentCallbackAutoConfiguration {

    @Bean
    fun refundCallbackMvcInterceptor(
        paymentProperties: PaymentProperties,
        paymentProviders : ObjectProvider<IPaymentProvider>,
        refundCallbackHandlers:ObjectProvider<IRefundCallbackHandler>
    ) : RefundCallbackMvcInterceptor {
        val providers = paymentProviders.orderedStream().collect(Collectors.toList())

        val handlers = refundCallbackHandlers.orderedStream().collect(Collectors.toList())
        return RefundCallbackMvcInterceptor(paymentProperties, providers, handlers)
    }

    @Bean
    @ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
    fun paymentCallbackMvcInterceptor(
        paymentProperties: PaymentProperties,
        paymentProviders : ObjectProvider<IPaymentProvider>,
        paymentCallbackHandlers:ObjectProvider<IPaymentCallbackHandler>
    ) : PaymentCallbackMvcInterceptor {
        val providers = paymentProviders.orderedStream().collect(Collectors.toList())

        val handlers = paymentCallbackHandlers.orderedStream().collect(Collectors.toList())
        return PaymentCallbackMvcInterceptor(paymentProperties, providers, handlers)
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
    protected class WeMvcInterceptorConfiguration(
        private val refundCallbackMvcInterceptor: RefundCallbackMvcInterceptor,
        private val paymentCallbackInterceptor: PaymentCallbackMvcInterceptor) : WebMvcConfigurer, IResourceAuthorizationConfigurer {


        override fun addInterceptors(registry: InterceptorRegistry) {
            super.addInterceptors(registry)
            val matcher = AntPathMatcher().apply {
                this.setCaseSensitive(false)
            }

            registry.addInterceptor(paymentCallbackInterceptor).apply {
                this.pathMatcher(matcher)
                this.addPathPatterns(PaymentProperties.PATH_PATTERN)
            }

            registry.addInterceptor(refundCallbackMvcInterceptor).apply {
                this.pathMatcher(matcher)
                this.addPathPatterns(PaymentProperties.PATH_REFUND_PATTERN)
            }
        }

        override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
            registry.antMatchers(PaymentProperties.PATH_PATTERN, ignoreCase = true).permitAll()
            registry.antMatchers(PaymentProperties.PATH_REFUND_PATTERN, ignoreCase = true).permitAll()
        }
    }
}