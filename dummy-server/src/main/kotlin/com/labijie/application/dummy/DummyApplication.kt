package com.labijie.application.dummy

import com.labijie.application.payment.annotation.EnablePaymentCallbackHandling
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import springfox.documentation.oas.annotations.EnableOpenApi
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@SpringBootApplication
@EnablePaymentCallbackHandling
@EnableGlobalMethodSecurity(prePostEnabled = true)
class DummyApplication

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    runApplication<DummyApplication>(*args)
}

