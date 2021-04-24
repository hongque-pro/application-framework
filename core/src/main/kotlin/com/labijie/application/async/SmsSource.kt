package com.labijie.application.async

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-28
 */
interface SmsSource {
    companion object {
        const val OUTPUT = "sms-output"
    }

    @Output(OUTPUT)
    fun output() : MessageChannel
}