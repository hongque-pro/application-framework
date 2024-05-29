/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.auth.configuration

import com.labijie.application.identity.model.RegisterBy
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("application.auth")
class AuthProperties {
    var registerBy: RegisterBy = RegisterBy.Phone
}