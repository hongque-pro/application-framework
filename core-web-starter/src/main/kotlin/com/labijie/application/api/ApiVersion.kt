package com.labijie.application.api

/**
 * @author Anders Xiao
 * @date 2025/9/18
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiVersion(val version: String, val requestPathPrefix: String = "")