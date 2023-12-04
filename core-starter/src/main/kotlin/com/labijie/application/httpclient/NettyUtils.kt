package com.labijie.application.httpclient

import com.labijie.application.SSLUtilities
import io.netty.channel.ChannelOption
import io.netty.handler.logging.LogLevel
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.SslProvider
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.io.FileInputStream
import java.io.InputStream
import java.security.KeyStore
import java.time.Duration
import javax.net.ssl.KeyManagerFactory


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
            .trustManager(SSLUtilities.NoneValidationTrustManager)
            .build()
    }

    fun createHttpClient(
        connectTimeOut: Duration,
        readTimeout: Duration,
        writeTimeOut: Duration,
        px12File: String? = null,
        password: String? = null,
        loggerEnabled: Boolean = false
    ): HttpClient {
        val logName = reactor.netty.http.client.HttpClient::class.java.name
        var client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut.toMillis().toInt())
            .compress(true)

        px12File?.let {
            client = client.secure {
                it.sslContext(createSslContext(px12File, password))
            }
        }
        client = client.doOnConnected { conn ->
            conn.addHandlerLast(ReadTimeoutHandler(readTimeout.seconds.toInt()))
            conn.addHandlerLast(WriteTimeoutHandler(writeTimeOut.seconds.toInt()))
        }.followRedirect(true).keepAlive(true)

        if (loggerEnabled) {
            client = client.wiretap(
                logName,
                LogLevel.DEBUG,
                AdvancedByteBufFormat.HEX_DUMP
            )
        }
        return client
    }


}