package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
@ConfigurationProperties(value ="application.validation")
class ValidationProperties {

    companion object {
        const val USER_NAME = "user-name"
        const val CODE = "code"
    }

    var regex:MutableMap<String, String> = mutableMapOf(
        USER_NAME to "(?=^.{3,16}\$)^[a-zA-Z][a-zA-Z0-9]*[_-]?[a-zA-Z0-9]+\$",
        CODE to "^(?!-)(?!.*?-\$)[a-z-]+\$"
    )
}