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
        const val CODE = "code"
    }

    var regex:MutableMap<String, String> = mutableMapOf(
        CODE to "^(?!-)(?!.*?-\$)[a-z-]+\$"
    )
}