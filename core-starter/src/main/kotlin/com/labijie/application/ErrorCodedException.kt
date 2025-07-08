package com.labijie.application

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
open class ErrorCodedException(
    val error: String,
    message: String? = null,
    cause: Throwable? = null,
    args: Map<String, String>? = null
) : ApplicationRuntimeException(message ?: localeErrorMessage(error), cause) {

    val args: MutableMap<String, String> = mutableMapOf()

    init {
        args?.forEach {
            this.args.putIfAbsent(it.key, it.value)
        }
    }

    open fun getDetails(): Map<String, Any>? {
        return args.ifEmpty { null }
    }
}