package com.labijie.application.web.controller

import com.labijie.application.IErrorRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
@RestController
class ErrorDescriptionController(private val errorRegistry: IErrorRegistry) {

    @GetMapping("/application-errors")
    fun errors(): Map<String, String> {
        return errorRegistry.errors
    }
}