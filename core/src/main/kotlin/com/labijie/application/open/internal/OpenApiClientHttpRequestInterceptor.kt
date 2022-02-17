package com.labijie.application.open.internal

import com.labijie.application.HttpFormUrlCodec
import com.labijie.application.configuration.OpenApiClientProperties
import com.labijie.application.open.OpenApiRequest
import com.labijie.application.open.OpenSignatureUtils
import com.labijie.infra.utils.ShortId
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.util.DefaultUriBuilderFactory

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
class OpenApiClientHttpRequestInterceptor(
    private val clientProperties: OpenApiClientProperties): ClientHttpRequestInterceptor {

    private val uriBuilderFactory = DefaultUriBuilderFactory()

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val queryString = request.uri.query
        val bodyString = if(body.isNotEmpty()) "" else body.toString(Charsets.UTF_8)

        val query = HttpFormUrlCodec.decode(queryString).toSingleValueMap()
        query[OpenSignatureUtils.TimestampField] = System.currentTimeMillis().toString()
        query[OpenSignatureUtils.AppIdField] = clientProperties.appid
        query[OpenSignatureUtils.SerialField] = ShortId.newId()

        val req = OpenApiRequest(HttpFormUrlCodec.encode(query), bodyString)

        query["sign"] = OpenSignatureUtils.sign(req, clientProperties.secret, clientProperties.algorithm)
        val newQuery = HttpFormUrlCodec.encode(query, Charsets.UTF_8)

        val builder = uriBuilderFactory.uriString(request.uri.toString())
        builder.replaceQuery(newQuery)
        val uri = builder.build()

        val r = ReplaceUriClientHttpRequest(uri, request)

        return execution.execute(r, body)
    }

}