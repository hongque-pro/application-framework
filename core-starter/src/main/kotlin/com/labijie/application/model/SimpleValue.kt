package com.labijie.application.model

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-17
 */
data class SimpleValue<T>(var value: T)

fun String.toSimpleValue(): SimpleValue<String>{
    return SimpleValue(this)
}

fun Int.toSimpleValue(): SimpleValue<Int>{
    return SimpleValue(this)
}

fun Long.toSimpleValue(): SimpleValue<Long>{
    return SimpleValue(this)
}

fun Boolean.toSimpleValue(): SimpleValue<Boolean>{
    return SimpleValue(this)
}

fun <T: Enum<T>> T.toSimpleValue(): SimpleValue<T>{
    return SimpleValue(this)
}