package com.labijie.application.conditional

import org.springframework.context.annotation.Conditional

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Conditional(ResourceServerCondition::class)
annotation class ConditionalOnResourceServer {
}