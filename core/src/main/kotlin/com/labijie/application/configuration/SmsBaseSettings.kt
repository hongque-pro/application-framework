package com.labijie.application.configuration

/**
 *
 * @author lishiwen
 * @date 20-5-18
 * @since JDK1.8
 */
data class SmsAsyncSettings(
    var sendTimeoutMinutes: Long = 5
)

data class SmsBaseSettings(
    var async: SmsAsyncSettings = SmsAsyncSettings()
)
