package com.labijie.application.model

import com.labijie.application.exception.InvalidOneTimeCodeException

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
data class OneTimeCodeVerifyResult(
    var success: Boolean = false,
    var target: OneTimeCodeTarget? = null
) {
    companion object {
        fun OneTimeCodeVerifyResult.getInputOrThrow() : OneTimeCodeTarget {
            return target ?: throw InvalidOneTimeCodeException()
        }
    }
}