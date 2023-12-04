package com.labijie.application.dapr.condition

import com.labijie.application.dapr.PubsubSide
import org.springframework.context.annotation.Conditional

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Conditional(DaprSideCondition::class)
annotation class ConditionalOnDaprPubsub(val side: PubsubSide)