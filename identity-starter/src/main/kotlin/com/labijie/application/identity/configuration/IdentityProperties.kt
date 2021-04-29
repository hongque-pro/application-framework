package com.labijie.application.identity.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:03
 * @Description:
 */
@ConfigurationProperties("application.identity")
data class IdentityProperties(
        var jdbcTablePrefix:String = "identity_",
        var cacheRegion:String = "",
        var cacheClientTimeout: Duration = Duration.ofMinutes(5),
        var cacheRoleTimeout: Duration = Duration.ofMinutes(10),
        var cacheUserTimeout: Duration = Duration.ofMinutes(10)
)