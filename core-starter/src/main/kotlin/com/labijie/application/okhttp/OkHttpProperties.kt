package com.labijie.application.okhttp

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.util.unit.DataSize
import java.time.Duration


/**
 * @author Anders Xiao
 * @date 2025/6/21
 */
@ConfigurationProperties(prefix = "application.okhttp")
class OkHttpProperties {

    /**
     * The interval between web socket pings initiated by this client. Use this to
     * automatically send web socket ping frames until either the web socket fails or it is closed.
     * This keeps the connection alive and may detect connectivity failures early. No timeouts are
     * enforced on the acknowledging pongs.
     *
     *
     * The default value of 0 disables client-initiated pings.
     */
    var pingInterval: Duration = Duration.ZERO


    /**
     * Whether to retry or not when a connectivity problem is encountered.
     */
    var retryOnConnectionFailure = true


    @NestedConfigurationProperty
    val cache = CacheProperties()

    @NestedConfigurationProperty
    val connectionPool = ConnectionPoolProperties()
}

/**
 * @author Lars Grefer
 * @see okhttp3.Cache
 */
class CacheProperties {
    var enabled = false
    /**
     * The maximum number of bytes this cache should use to store.
     */
    var maxSize: DataSize = DataSize.ofMegabytes(100)

    /**
     * The path of the directory where the cache should be stored.
     */
    var directory: String = "okhttp-cache"
}

class ConnectionPoolProperties {
    /**
     * The maximum number of idle connections for each address.
     */
    var maxIdleConnections = 3

    var keepAliveDuration: Duration = Duration.ofMinutes(5)
}