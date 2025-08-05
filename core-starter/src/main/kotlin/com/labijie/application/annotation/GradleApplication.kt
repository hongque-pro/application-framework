package com.labijie.application.annotation

/**
 * @author Anders Xiao
 * @date 2025/8/5
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class GradleApplication(val projectGroup: String, val projectName: String = "")