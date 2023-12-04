package com.labijie.application.dummy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@EnableWebSecurity
@EnableWebMvc
@SpringBootApplication
class DummyApplication

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    runApplication<DummyApplication>(*args)

}

