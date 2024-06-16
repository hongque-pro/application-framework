package com.labijie.application

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

object HttpFormUrlCodec {

    fun encode(formData: Map<String, Any?>, charset: Charset = Charsets.UTF_8, allowNullValue: Boolean = false) : String {
        val builder = StringBuilder()
        formData.forEach { (name, value) ->
            if(value != null || allowNullValue) {
                builder.appendNameValue(name, value ?: "", charset)
            }
        }
        return builder.toString()
    }

    private fun StringBuilder.appendNameValue(
        name: String,
        value: Any,
        charset: Charset
    ) {
        if (this.isNotEmpty()) {
            this.append('&')
        }
        this.append(URLEncoder.encode(name, charset.name()))
        val stringValue = value.toString()
        if (stringValue.isNotBlank()) {
            this.append('=')
            this.append(URLEncoder.encode(stringValue, charset.name()))
        }
    }

    fun encode(formData: MultiValueMap<String, Any>, charset: Charset = Charsets.UTF_8) : String {
        val builder = StringBuilder()
        formData.forEach { (name, values) ->
            values.forEach { value ->
                builder.appendNameValue(name, value, charset)
            }
        }
        return builder.toString()
    }

    fun decode(content:ByteArray, charset:Charset = Charsets.UTF_8): MultiValueMap<String, String> {
        val contentString = content.toString(charset)
        return decode(contentString, charset)
    }

    fun decode(content:String, charset:Charset = Charsets.UTF_8): MultiValueMap<String, String> {
        val pairs = StringUtils.tokenizeToStringArray(content, "&")
        val result = LinkedMultiValueMap<String, String>(pairs.size)
        for (pair in pairs) {
            val idx = pair.indexOf('=')
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset.name()), null)
            } else {
                val name = URLDecoder.decode(pair.substring(0, idx), charset.name())
                val value = URLDecoder.decode(pair.substring(idx + 1), charset.name())
                result.add(name, value)
            }
        }
        return result
    }
}