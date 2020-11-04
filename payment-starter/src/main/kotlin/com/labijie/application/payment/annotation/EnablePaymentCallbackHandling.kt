package com.labijie.application.payment.annotation

import com.labijie.application.payment.configuration.PaymentCallbackAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * 在 MVC 中对支付回调进行拦截
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Import(PaymentCallbackAutoConfiguration::class)
annotation class EnablePaymentCallbackHandling