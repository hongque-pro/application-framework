package com.labijie.application

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
@Target(AnnotationTarget.FIELD)
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorDescription(val description:String, val locale: String = Constants.ZH_CN)