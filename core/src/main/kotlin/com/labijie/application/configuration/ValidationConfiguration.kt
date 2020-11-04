package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
@ConfigurationProperties(value ="application.validation")
class ValidationConfiguration {

    companion object {
        const val USER_NAME = "user-name"
        const val PHONE_NUMBER = "phone-number"
        const val CODE = "code"
    }

    var regex:MutableMap<String, String> = mutableMapOf(
        USER_NAME to "(?=^.{3,16}\$)^[a-zA-Z][a-zA-Z0-9]*[_-]?[a-zA-Z0-9]+\$",
        PHONE_NUMBER to "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}\$",
        CODE to "^(?!-)(?!.*?-\$)[a-z-]+\$"
    )
}