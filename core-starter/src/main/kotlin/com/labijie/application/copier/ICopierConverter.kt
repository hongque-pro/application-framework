package com.labijie.application.copier

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 */
interface ICopierConverter {
    fun isSupported(source:Class<*>, target:Class<*>) : Boolean

    fun convert(source: Any?, target: Class<*>): Any?
}