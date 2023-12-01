package com.labijie.application.netty

import com.labijie.application.configuration.HttpClientProperties
import io.netty.handler.logging.LogLevel
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.SslProvider
import org.springframework.http.client.ReactorNettyClientRequestFactory
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.io.FileInputStream
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory


/**
 * @author Anders Xiao
 * @date 2023-11-28
 */
object NettyUtils {
    private fun createSslContext(px12File: String, password: String?): SslContext {

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())

        val stream: InputStream = FileInputStream(px12File)
        val keyStore = KeyStore.getInstance("PKCS12")
        stream.use {
            keyStore.load(it, password?.toCharArray())
        }
        keyManagerFactory.init(keyStore, password?.toCharArray())

        return SslContextBuilder.forClient()
            .sslProvider(SslProvider.JDK)
            .keyManager(keyManagerFactory)
            .trustManager(TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()))
            .build()
    }

    fun createHttpClient(px12File: String? = null, password: String? = null, loggerEnabled: Boolean = false): HttpClient {
        val client = HttpClient.create()
        client.compress(true)
        if(px12File != null) {
            client.secure {
                it.sslContext(createSslContext(px12File, password))
            }
        }

        client.disableRetry(true)
        client.followRedirect(true)
        client.keepAlive(true)
        if(loggerEnabled) {
            client.wiretap(reactor.netty.http.client.HttpClient::class.java.simpleName, LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
        }
        return client
    }

    fun ReactorNettyClientRequestFactory.configure(properties: HttpClientProperties){
        this.setConnectTimeout(properties.connectTimeout)
        this.setReadTimeout(properties.readTimeout)
        this.setExchangeTimeout(properties.writeTimeout)
    }

}