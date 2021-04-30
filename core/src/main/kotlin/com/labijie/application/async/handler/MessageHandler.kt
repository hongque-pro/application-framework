package com.labijie.application.async.handler

import com.labijie.application.component.IMessageSender
import com.labijie.application.model.SendSmsTemplateParam
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.context.annotation.Bean
import java.util.function.Consumer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-06
 */
class MessageHandler(
    private val messageSender: IMessageSender) {

    companion object {
        const val STREAM_SMS_OUT = "handleSms-out-0"
        const val STREAM_SMS_IN = "handleSms-in-0"
    }

    @Bean
    fun handleSms() : Consumer<SendSmsTemplateParam> {
        return Consumer {
            messageSender.sendSmsTemplate(it, async = false, checkTimeout = true)
        }
    }
}