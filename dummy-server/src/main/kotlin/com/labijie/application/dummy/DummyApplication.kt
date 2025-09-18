package com.labijie.application.dummy

import com.labijie.application.annotation.GradleApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@EnableWebSecurity
@SpringBootApplication
@GradleApplication(projectGroup = "com.labijie.application", projectName = "dummy-server")
class DummyApplication

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    runApplication<DummyApplication>(*args)

}

