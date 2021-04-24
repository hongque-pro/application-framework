package com.labijie.application.async.handler

import com.labijie.application.async.SmsSink
import com.labijie.application.component.IMessageSender
import com.labijie.application.model.SendSmsTemplateParam
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-06
 */
@EnableBinding(value =[SmsSink::class])
class MessageHandler(
    private val messageSender: IMessageSender) {
    @StreamListener(SmsSink.INPUT)
    fun handleSms(param: SendSmsTemplateParam) {
        messageSender.sendSmsTemplate(param, async = false, checkTimeout = true)
    }
}