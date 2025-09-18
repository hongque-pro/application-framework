package com.labijie.application.dummy.controller

import com.labijie.application.api.ApiVersion
import com.labijie.application.getOneTimeCodeInRequest
import com.labijie.application.model.OneTimeCodeVerifyRequest
import com.labijie.application.model.SimpleValue
import com.labijie.application.web.annotation.OneTimeCodeVerify
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import javax.print.attribute.standard.Media

@RestController
@RequestMapping("/test2")
class Test2Controller(builder: RestClient.Builder) {


    @GetMapping("/test")
    fun test(): String {
        return "ok"
    }

    @OneTimeCodeVerify
    @PostMapping("/test")
    fun testOneTimeCode(): String {
        return "ok"
    }

    @OneTimeCodeVerify
    @PostMapping("/test")
    @ApiVersion("2.0")
    fun testOneTimeCodeV2(): String {
        return "ok"
    }

    @PostMapping("/totp")
    fun testOneTimeCode2(
        request: HttpServletRequest,
    ): SimpleValue<OneTimeCodeVerifyRequest?> {
        val c = request.getOneTimeCodeInRequest()
        return SimpleValue(c)
    }
}