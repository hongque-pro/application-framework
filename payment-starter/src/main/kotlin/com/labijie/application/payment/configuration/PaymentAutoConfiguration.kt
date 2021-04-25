package com.labijie.application.payment.configuration

import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.PaymentErrorsRegistration
import com.labijie.application.payment.PaymentUtils
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.providers.alipay.AlipayPaymentProvider
import com.labijie.application.payment.providers.alipay.IAlipayPaymentProviderCustomizer
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import com.labijie.application.payment.providers.wechat.WechatPaymentProvider
import com.labijie.application.payment.providers.wechat.IWechatPaymentProviderCustomizer
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.util.stream.Collectors

@Configuration(proxyBeanMethods = false)
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@EnableConfigurationProperties(PaymentProperties::class)
class PaymentAutoConfiguration {
    companion object{
        const val PROVIDERS_CONFIG_PREFIX = "application.pay.providers"
    }

    @Bean
    fun paymentErrorsRegistration(): PaymentErrorsRegistration {
        return PaymentErrorsRegistration()
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value= ["$PROVIDERS_CONFIG_PREFIX.alipay.enabled"], matchIfMissing = true)
    @EnableConfigurationProperties(AlipayPaymentOptions::class)
    protected class AlipayConfiguration {

        @Bean
        fun alipayPaymentProvider(
            paymentProperties: PaymentProperties,
            restTemplates: MultiRestTemplates,
            alipayPaymentOptions: AlipayPaymentOptions,
            customizers: ObjectProvider<IAlipayPaymentProviderCustomizer>
        ): AlipayPaymentProvider {
            return AlipayPaymentProvider(
                paymentProperties,
                alipayPaymentOptions,
                restTemplates
            ).apply {
                customizers.orderedStream().forEach {
                    it.customize(this)
                }
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value= ["$PROVIDERS_CONFIG_PREFIX.wechat.enabled"], matchIfMissing = true)
    @EnableConfigurationProperties(WechatPaymentOptions::class)
    protected class WechatConfiguration {
        @Bean
        fun wechatPaymentProvider(
            networkConfig: NetworkConfig,
            paymentProperties: PaymentProperties,
            restTemplates: MultiRestTemplates,
            wechatPaymentOptions: WechatPaymentOptions,
            customizers: ObjectProvider<IWechatPaymentProviderCustomizer>
        ): WechatPaymentProvider {
            return WechatPaymentProvider(
                paymentProperties,
                networkConfig,
                wechatPaymentOptions,
                restTemplates
            ).apply {
                customizers.orderedStream().forEach {
                    it.customize(this)
                }
            }
        }
    }

    @Bean
    fun paymentUrils(paymentProviders: ObjectProvider<IPaymentProvider>): PaymentUtils{
        val providers = paymentProviders.orderedStream().collect(Collectors.toList())
        val providerMap = providers.map {
            it.name to it
        }.toMap()

        return PaymentUtils(providerMap)
    }
}