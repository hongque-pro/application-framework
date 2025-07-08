package com.labijie.application.okhttp

import okhttp3.OkHttpClient
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings.Redirects
import org.springframework.util.Assert
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


class OkHttpClientRequestFactoryBuilder(
    private val okHttpClient: OkHttpClient) : ClientHttpRequestFactoryBuilder<OkHttpClientRequestFactory> {

    override fun build(): OkHttpClientRequestFactory {
        return this.build(null)
    }

    override fun build(settings: ClientHttpRequestFactorySettings?): OkHttpClientRequestFactory {
        val builder = okHttpClient.newBuilder()
        if (settings == null) {
            return OkHttpClientRequestFactory(builder.build())
        }

        val connectTimeout = settings.connectTimeout()
        if (connectTimeout != null) {
            builder.connectTimeout(connectTimeout)
        }
        settings.connectTimeout()?.let {
            builder.connectTimeout(it)
        }

        settings.readTimeout()?.let {
            builder.readTimeout(it)
        }

        settings.sslBundle()?.let {
            sslBundle->
            Assert.state(!sslBundle.options.isSpecified, "SSL Options cannot be specified with OkHttp")

            val sslContext = sslBundle.createSslContext()
            val socketFactory: SSLSocketFactory = sslContext.socketFactory

            val trustManagers = sslBundle.managers.trustManagers
            Assert.state(
                trustManagers.size == 1,
                "Trust material must be provided in the SSL bundle for OkHttp3ClientHttpRequestFactory"
            )

            builder.sslSocketFactory(socketFactory, (trustManagers[0] as X509TrustManager?)!!)
        }

        settings.redirects()?.let {
            redirects->
            when (redirects) {
                Redirects.FOLLOW_WHEN_POSSIBLE, Redirects.FOLLOW -> {
                    builder.followRedirects(true)
                    builder.followSslRedirects(true)
                }

                Redirects.DONT_FOLLOW -> {
                    builder.followRedirects(false)
                    builder.followSslRedirects(true)
                }
            }
        }

        return OkHttpClientRequestFactory(builder.build())
    }
}