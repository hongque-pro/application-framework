package com.labijie.application.dummy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@SpringBootApplication
class DummyApplication

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    runApplication<DummyApplication>(*args)
}

