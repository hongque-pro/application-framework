package com.labijie.application.dapr.configuration

import com.labijie.application.JsonMode
import com.labijie.infra.utils.ifNullOrBlank
import io.dapr.config.Properties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * @author Anders Xiao
 * @date 2023-12-04
 *
 * some environment refer:
 * https://docs.dapr.io/reference/environment/
 */
@ConfigurationProperties("application.dapr")
class DaprProperties {
    var pubServiceEnabled: Boolean = false
    var subServiceEnabled: Boolean = false
    var jsonMode: JsonMode = JsonMode.NORMAL
    var shutdownDaprOnExit: Boolean = true


    @NestedConfigurationProperty
    val messageService: MessageServiceConfig = MessageServiceConfig()


    val appId: String by lazy {
        System.getenv("APP_ID").ifNullOrBlank { "" }
    }


    val getDaprHttpPort: Int by lazy {
        Properties.HTTP_PORT.get()
    }

    val daprGrpcPort: Int by lazy {
        Properties.GRPC_PORT.get()
    }

    val daprApiToken: String by lazy {
        Properties.API_TOKEN.get()
    }
}
