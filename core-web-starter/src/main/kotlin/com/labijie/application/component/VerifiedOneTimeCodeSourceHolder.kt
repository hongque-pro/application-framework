package com.labijie.application.component

import com.labijie.application.model.OneTimeCodeVerifyResult

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
object VerifiedOneTimeCodeSourceHolder {
    private val holder: ThreadLocal<OneTimeCodeVerifyResult?> = ThreadLocal()

    fun set(value: OneTimeCodeVerifyResult) {
        holder.set(value)
    }

    fun get(): OneTimeCodeVerifyResult? {
        return holder.get()
    }

    fun clear() {
        if(holder.get() != null) {
            holder.remove()
        }
    }
}