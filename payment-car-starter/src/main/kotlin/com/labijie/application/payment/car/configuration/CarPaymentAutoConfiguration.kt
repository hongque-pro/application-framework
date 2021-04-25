
package com.labijie.application.payment.car.configuration

import com.labijie.application.payment.car.alipay.AlipayCarPaymentProvider
import com.labijie.application.payment.car.wechat.WechatCarPaymentOptions
import com.labijie.application.payment.car.wechat.WechatCarPaymentProvider
import com.labijie.application.payment.car.wechat.WechatPaymentCustomizer
import com.labijie.application.payment.configuration.PaymentAutoConfiguration
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.providers.alipay.AlipayPaymentProvider
import com.labijie.application.payment.providers.alipay.IAlipayPaymentProviderCustomizer
import com.labijie.application.payment.providers.wechat.IWechatPaymentProviderCustomizer
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import com.labijie.application.payment.providers.wechat.WechatPaymentProvider
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration(proxyBeanMethods = false)
class CarPaymentAutoConfiguration {

    @Bean
    fun wechatPaymentParkingCustomizer(): WechatPaymentCustomizer {
        return WechatPaymentCustomizer()
    }

    @ConditionalOnBean(AlipayPaymentOptions::class)
    @Configuration(proxyBeanMethods = false)
    protected class AlipayConfiguration {

        @Bean
        fun alipayCarPaymentProvider(
            paymentProperties: PaymentProperties,
            restTemplates: MultiRestTemplates,
            alipayPaymentOptions: AlipayPaymentOptions
        ): AlipayCarPaymentProvider {
            return AlipayCarPaymentProvider(
                paymentProperties,
                alipayPaymentOptions,
                restTemplates
            )
        }
    }

    @ConditionalOnBean(WechatPaymentOptions::class)
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(WechatCarPaymentOptions::class)
    protected class WechatConfiguration {
        @Bean
        fun wechatCarPaymentProvider(
            wechatCarPaymentOptions: WechatCarPaymentOptions,
            networkConfig: NetworkConfig,
            paymentProperties: PaymentProperties,
            restTemplates: MultiRestTemplates,
            wechatPaymentOptions: WechatPaymentOptions,
            customizers: ObjectProvider<IWechatPaymentProviderCustomizer>
        ): WechatCarPaymentProvider {
            return WechatCarPaymentProvider(
                paymentProperties,
                networkConfig,
                wechatPaymentOptions,
                restTemplates,
                wechatCarPaymentOptions
            )
        }
    }
}