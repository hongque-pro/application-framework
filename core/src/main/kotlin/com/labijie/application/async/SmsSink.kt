package com.labijie.application.async

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-06
 */
interface SmsSink {
    companion object {
        const val INPUT = "sms-input"
    }
    @Input(INPUT)
    fun input() : SubscribableChannel
}