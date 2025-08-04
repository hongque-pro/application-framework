package com.labijie.application.dummy.controller

import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.toSimpleValue
import com.labijie.application.validation.PhoneNumber
import com.labijie.application.web.annotation.HttpCache
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.infra.oauth2.AccessToken
import com.labijie.infra.oauth2.filter.ClientRequired
import com.labijie.infra.oauth2.mvc.OAuth2ClientLoginResponse
import com.labijie.infra.utils.logger
import jakarta.annotation.security.PermitAll
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
@RestController
@RequestMapping("/test")
open class TestingController {

    init {
        logger.warn("${TestingController::class.simpleName} loaded")
    }

    @PhoneNumber("country", "number")
    class PhoneTest {
        var country: Short = 0

        var number: String = ""
    }

    @Autowired(required = false)
    private lateinit var restTemplate: RestTemplate


    @PermitAll
    @PostMapping("/invalid-phone-number")
    fun testResponse(
        @RequestBody @Valid phone: RegisterInfo
    ): RegisterInfo {
        return phone
    }

    @PermitAll
    @PostMapping("/invalid-phone-number-not-blank")
    fun testResponse(
        @RequestBody @Valid phone: PhoneTest
    ): PhoneTest {
        return phone
    }

    @HumanVerify
    @GetMapping("/test-human-verify")
    fun testHumanVerify(): SimpleValue<String> {
        return "OK".toSimpleValue()
    }


    @GetMapping("/array")
    fun getUrl(@RequestParam param: TestEnum, @RequestParam param2: TestEnum? = null) : SimpleValue<String> {
        return "OK".toSimpleValue()
    }

    @GetMapping("/model")
    fun getModel() : TestMode {
        return TestMode()
    }

    @GetMapping("/bing")
    fun bing() : SimpleValue<String> {
        val entity = restTemplate.getForEntity("https://bing.com", String::class.java)
        return entity.toString().toSimpleValue()
    }

    @HumanVerify
    @GetMapping("/human-verify")
    fun human() : SimpleValue<String> {
        val entity = restTemplate.getForEntity("https://bing.com", String::class.java)
        return entity.toString().toSimpleValue()
    }

    @ClientRequired
    @GetMapping("/client-verify")
    fun clientRequired() : SimpleValue<String> {
        val entity = restTemplate.getForEntity("https://bing.com", String::class.java)
        return entity.toString().toSimpleValue()
    }

    @HttpCache(maxAge = 3600)
    @GetMapping("/cache-control")
    fun cacheControl() : SimpleValue<String> {
        val entity = restTemplate.getForEntity("https://bing.com", String::class.java)
        return entity.toString().toSimpleValue()
    }

    public class TestMode
    {
        var name:String = "ddd"
        var id:Long = 0
    }
}