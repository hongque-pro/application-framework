package com.labijie.application.web.client

import com.labijie.infra.utils.logger
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseExtractor

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
object StringResponseExtractor : ResponseExtractor<ResponseEntity<String?>> {
    override fun extractData(response: ClientHttpResponse): ResponseEntity<String?> {
        val responseWrapper = MessageBodyClientHttpResponseWrapper(response)
        if (!responseWrapper.hasMessageBody() || responseWrapper.hasEmptyMessageBody()) {
            return ResponseEntity.status(response.statusCode).headers(response.headers).body(null)
        }
        val contentType = getContentType(responseWrapper)

        val bodyString = responseWrapper.body.readBytes().toString(contentType?.charset ?: Charsets.UTF_8)

        return ResponseEntity.status(response.statusCode).headers(response.headers).body(bodyString)
    }

    private fun getContentType(response: ClientHttpResponse): MediaType {
        var contentType = response.headers.contentType
        if (contentType == null) {
            if (logger.isDebugEnabled) {
                logger.debug("No content-type, using 'application/octet-stream'")
            }
            contentType = MediaType.APPLICATION_OCTET_STREAM
        }
        return contentType
    }

}