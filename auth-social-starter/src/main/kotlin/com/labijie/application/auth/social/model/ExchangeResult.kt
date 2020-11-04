package com.labijie.application.auth.social.model

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class ExchangeResult<T> private constructor(val data: T?) {
    companion object {
        fun <T> success(data: T): ExchangeResult<T> {
            return ExchangeResult(data)
        }

        fun <T> failure(): ExchangeResult<T> {
            return ExchangeResult(null)
        }
    }

    val isSuccess:Boolean
    get() = data != null
}