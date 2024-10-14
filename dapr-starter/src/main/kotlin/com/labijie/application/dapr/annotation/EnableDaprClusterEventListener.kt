/**
 * @author Anders Xiao
 * @date 2024-10-14
 */
package com.labijie.application.dapr.annotation

import com.labijie.application.dapr.configuration.ClusterEventListenerImportSelector
import org.springframework.context.annotation.Import


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(ClusterEventListenerImportSelector::class)
annotation class EnableDaprClusterEventListener(val events: Array<String> = ["*"], val excludeEvents:Array<String> = [])