package com.labijie.application.dummy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/verify")
class AuthRequiredController {

    @GetMapping("/text")
    fun text():String{
        return "SUCCESS"
    }
}