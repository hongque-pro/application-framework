package com.labijie.application.dummy

import com.labijie.application.dummy.configuration.AppResourceConfiguration
import com.labijie.application.dummy.controller.TestingController
import com.labijie.application.order.PaymentStatus
import com.labijie.application.order.component.IOrderAdapter
import com.labijie.application.order.models.NormalizedOrder
import com.labijie.application.order.models.PaymentEffect
import com.labijie.application.payment.PaymentUtils
import com.labijie.application.payment.PlatformTrade
import com.labijie.application.payment.annotation.EnablePaymentCallbackHandling
import com.labijie.application.payment.car.CarPaymentProviders
import com.labijie.application.payment.car.scene.CarUserGuideScene
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import com.labijie.infra.oauth2.annotation.EnableOAuth2Server
import com.labijie.infra.oauth2.annotation.OAuth2ServerType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*
import kotlin.reflect.KClass

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@SpringBootApplication
@EnablePaymentCallbackHandling
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableOAuth2Server(include = [OAuth2ServerType.Resource, OAuth2ServerType.Authorization])
class DummyApplication

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    runApplication<DummyApplication>(*args)
}

