package com.labijie.application.web.client

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */


fun RestTemplate.exchangeForString(uri: String, method: HttpMethod, entity: HttpEntity<*>, vararg uriVariables: Any): ResponseEntity<String?> {
    val callback = this.httpEntityCallback<String>(entity, String::class.java)
    return this.execute(uri, method, callback, StringResponseExtractor, *uriVariables)!!
}

fun RestTemplate.exchangeForString(uri: String, method: HttpMethod, entity: HttpEntity<*>, uriVariables: Map<String, *>): ResponseEntity<String?> {
    val callback = this.httpEntityCallback<String>(entity, String::class.java)
    return this.execute(uri, method, callback, StringResponseExtractor, uriVariables)!!
}