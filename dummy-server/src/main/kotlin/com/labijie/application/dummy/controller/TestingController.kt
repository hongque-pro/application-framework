package com.labijie.application.dummy.controller

import com.labijie.application.component.IMessageSender
import com.labijie.application.model.CaptchaType
import com.labijie.application.model.SendSmsCaptchaParam
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
@RestController
@RequestMapping("/test")
open class TestingController {

    init {
        logger.info("${TestingController::class.simpleName} loaded")
    }
    @Autowired
    private lateinit var messageSender: IMessageSender

    @GetMapping("/sms")
    fun sms(){
        val param = SendSmsCaptchaParam().apply {
            this.captchaType = CaptchaType.Login
            this.modifier = "ttt"
            this.phoneNumber = "13000000000"
            this.stamp = ShortId.newId()
        }
        messageSender.sendSmsCaptcha(param)
    }

    @GetMapping("/array")
    fun getUrl(@RequestParam param:Array<String>) : String {
        return param.joinToString()
    }

    @GetMapping("/model")
    fun getModel() : TestMode {
        return TestMode()
    }

    public class TestMode
    {
        var name:String = "ddd"
        var id:Long = 0
    }
}