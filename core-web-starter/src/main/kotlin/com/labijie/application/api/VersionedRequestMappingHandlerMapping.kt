/**
 * @author Anders Xiao
 * @date 2025/9/18
 */
package com.labijie.application.api

import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.condition.RequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method


class VersionedRequestMappingHandlerMapping : RequestMappingHandlerMapping() {
    override fun getCustomTypeCondition(handlerType: Class<*>): RequestCondition<*>? {
        return createRequestCondition(handlerType)
    }

    override fun getCustomMethodCondition(method: Method): RequestCondition<*>? {
        return createRequestCondition(method)
    }

    private fun createRequestCondition(target: AnnotatedElement): RequestCondition<ApiVersionRequestCondition>? {
        val apiVersion: ApiVersion? = AnnotationUtils.findAnnotation(target, ApiVersion::class.java)
        if (apiVersion == null) {
            return null
        }
        val version = apiVersion.version.trim()
        InnerUtils.checkVersionNumber(version, target)
        return ApiVersionRequestCondition(apiVersion)
    }

    //--------------------- 动态注册URI -----------------------//
    protected override fun getMappingForMethod(method: Method, handlerType: Class<*>): RequestMappingInfo? {
        var ignoreTypeCondition = false

        var apiVersion: ApiVersion? = AnnotationUtils.getAnnotation(method, ApiVersion::class.java)
        if (apiVersion == null) {
            apiVersion = AnnotationUtils.getAnnotation(handlerType, ApiVersion::class.java)
        }else {
            ignoreTypeCondition = true
        }

        if(apiVersion == null) {
            return super.getMappingForMethod(method, handlerType)
        }

        var info: RequestMappingInfo? = this.getElementMapping(method)
        if (info != null) {
            val typeInfo: RequestMappingInfo? = this.getElementMapping(handlerType, ignoreTypeCondition)
            if (typeInfo != null) {
                info = typeInfo.combine(info)
            }

            // 指定URL前缀
            var apiVersion: ApiVersion? = AnnotationUtils.getAnnotation(method, ApiVersion::class.java)
            if (apiVersion == null) {
                apiVersion = AnnotationUtils.getAnnotation(handlerType, ApiVersion::class.java)
            }
            if (apiVersion != null) {
                val version = apiVersion.version.trim()
                InnerUtils.checkVersionNumber(version, method)

                val prefix = "${apiVersion.requestPathPrefix.trim()}/v$version"
                info = RequestMappingInfo.paths(*arrayOf(prefix))
                    .build().combine(info)
            }
        }

        return info
    }

    private fun getElementMapping(element: AnnotatedElement, ignoreVersion: Boolean = false): RequestMappingInfo? {
        val requestMapping: RequestMapping? =
            AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping::class.java)

        if (requestMapping == null) return null

        if(ignoreVersion) {
            return this.createRequestMappingInfo(requestMapping, null)
        }

        val condition = when (element) {
            is Class<*> -> this.getCustomTypeCondition(element)
            is Method -> this.getCustomMethodCondition(element)
            else -> return null
        }
        return this.createRequestMappingInfo(requestMapping, condition)
    }
}