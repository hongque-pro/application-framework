/**
 * @author Anders Xiao
 * @date 2024-08-19
 */
package com.labijie.application.annotation

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Inherited
@Import(ErrorBeanDefinitionRegister::class)
annotation class ImportErrorDefinition(val classes: Array<KClass<*>>)