package com.labijie.application.snowflake

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-05-01 11:09
 * @Description:
 */
@ConfigurationProperties("infra.snowflake.jdbc")
data class JdbcSlotProviderProperties(
        var instanceIdentity: InstanceIdentity = InstanceIdentity.UUID,
        var timeout: Duration = Duration.ofMinutes(3)
)