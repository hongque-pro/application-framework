package com.labijie.application.web.controller

import com.labijie.application.IErrorRegistry
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
@RestController
class ErrorDescriptionController: ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    private val errorRegistry: IErrorRegistry by lazy {
        applicationContext.getBean(IErrorRegistry::class.java)
    }

    @GetMapping("/application-errors")
    fun errors(): Map<String, String> {
        return errorRegistry.errors
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}