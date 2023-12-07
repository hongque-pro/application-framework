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
    localizedMessage: String? = null
) : ApplicationRuntimeException(message, cause){

    private val locMessage: String = localizedMessage ?: localeErrorMessage(error)
    open fun getDetails():Map<String, Any>? {
        return null
    }

    override fun getLocalizedMessage(): String = locMessage

    override val message: String?
        get() = super.message ?: localizedMessage
}