/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.auth.configuration

import com.labijie.application.identity.model.RegisterBy
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.time.Duration


@ConfigurationProperties("application.auth")
class AuthProperties {
    var registerBy: RegisterBy = RegisterBy.Phone
    var registerControllerEnabled: Boolean = true
    var securitySecretKey = "c:zjLjK6JmKdRVw"

    @NestedConfigurationProperty
    val oauth2Login = OAuth2LoginSettings()
}