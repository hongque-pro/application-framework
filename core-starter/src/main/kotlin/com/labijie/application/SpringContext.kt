package com.labijie.application

import com.labijie.infra.isDevelopment
import org.springframework.context.ApplicationContext

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
object SpringContext {
    lateinit var current: ApplicationContext

    val isInitialized:Boolean
    get() = SpringContext::current.isInitialized

    val isDevelopment by lazy {
        isInitialized && current.environment.isDevelopment
    }

    val isTest by lazy {
        isInitialized && current.environment.activeProfiles.contains("test")
    }
}