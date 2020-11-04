package com.labijie.application.auth.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Author: Anders Xiao
 * Date: Created in 2020/6/3 10:41
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
@ConfigurationProperties("application.auth")
data class AuthServerProperties(
    var jdbcTablePrefix:String = "identity_",
    var cacheRegion:String = "",
    var cacheClientTimeout:Duration = Duration.ofMinutes(5),
    var cacheRoleTimeout:Duration = Duration.ofMinutes(10),
    var cacheUserTimeout:Duration = Duration.ofMinutes(10)
)