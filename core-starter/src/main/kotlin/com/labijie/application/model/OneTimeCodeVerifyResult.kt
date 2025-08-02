package com.labijie.application.model

import com.labijie.application.exception.InvalidOneTimeCodeException

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
data class OneTimeCodeVerifyResult(
    var success: Boolean = false,
    var input: OneTimeCodeTarget? = null
) {
    companion object {
        fun OneTimeCodeVerifyResult.getInputOrThrow() : OneTimeCodeTarget {
            return input ?: throw InvalidOneTimeCodeException()
        }
    }
}