package com.labijie.application.exception

import com.labijie.application.ApplicationRuntimeException
import com.labijie.infra.utils.ifNullOrBlank

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 16:36
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
class ThirdPartyExchangeException(
    platformName: String,
    message: String? = null,
    cause: Throwable? = null,
    val platformErrorCode:String? = null
) : ApplicationRuntimeException(if(message.isNullOrBlank())
    "Exchange with 3rd server fault ( platform: $platformName, error code: ${platformErrorCode.ifNullOrBlank("<null>")} ) ."
    else "$message (platform: $platformName, error code: ${platformErrorCode.ifNullOrBlank("<null>")})", cause) {

}