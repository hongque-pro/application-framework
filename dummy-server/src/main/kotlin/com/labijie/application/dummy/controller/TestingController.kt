package com.labijie.application.dummy.controller

import com.labijie.application.component.IMessageService
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.SmsCodeType
import com.labijie.application.model.toSimpleValue
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
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
    @Autowired(required = false)
    private lateinit var messageSvc: IMessageService

    @Autowired(required = false)
    private lateinit var restTemplate: RestTemplate


    @GetMapping("/array")
    fun getUrl(@RequestParam param:Array<String>) : String {
        return param.joinToString()
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

    public class TestMode
    {
        var name:String = "ddd"
        var id:Long = 0
    }
}