/**
 * @author Anders Xiao
 * @date 2024-02-21
 */
package com.labijie.application.configuration

import com.labijie.application.JsonMode
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("application.web")
data class ApplicationWebProperties(
    var jsonMode: JsonMode = JsonMode.JAVASCRIPT
)