package com.labijie.application.okhttp

import okhttp3.OkHttpClient
import org.springframework.beans.factory.DisposableBean
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpRequestFactory
import java.net.URI


class OkHttpClientRequestFactory(private val okHttpClient: OkHttpClient) : ClientHttpRequestFactory, DisposableBean {
    override fun createRequest(
        uri: URI,
        httpMethod: HttpMethod
    ): ClientHttpRequest {
        return OkHttpClientRequest(okHttpClient, uri, httpMethod)
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

}