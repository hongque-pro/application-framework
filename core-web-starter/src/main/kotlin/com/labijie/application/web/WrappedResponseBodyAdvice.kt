package com.labijie.application.web

import com.labijie.application.web.annotation.ResponseWrapped
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-17
 */
@RestControllerAdvice
class WrappedResponseBodyAdvice : ResponseBodyAdvice<Any> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return AbstractJackson2HttpMessageConverter::class.java.isAssignableFrom(converterType)
                && (returnType.annotatedElement.isAnnotationPresent(ResponseWrapped::class.java) ||
                AnnotationUtils.isAnnotationDeclaredLocally(
                    ResponseWrapped::class.java,
                    returnType.executable.declaringClass
                ))
    }

    override fun beforeBodyWrite(
        @Nullable body: Any?, returnType: MethodParameter,
        contentType: MediaType, converterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest, response: ServerHttpResponse
    ): Any? {
        return getOrCreateContainer(body.asRestResponse())
    }

    protected fun getOrCreateContainer(body: Any): MappingJacksonValue {
        return body as? MappingJacksonValue ?: MappingJacksonValue(body)
    }


}