/**
 * @author Anders Xiao
 * @date 2023-12-12
 */
package com.labijie.application.auth.configuration

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("application.default-user-creation")
class DefaultUserCreationProperties {
    var enabled: Boolean = false
    var username: String = "admin"
    var password: String = ""
    var roles: String = "admin"
}