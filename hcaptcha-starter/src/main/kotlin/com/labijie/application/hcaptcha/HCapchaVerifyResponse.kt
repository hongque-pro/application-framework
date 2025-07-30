package com.labijie.application.hcaptcha

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

/**
 * @author Anders Xiao
 * @date 2025/7/29
 *
 * refer: https://docs.hcaptcha.com/#verify-the-user-response-server-side
 */
class HCapchaVerifyResponse {
    var success: Boolean = false

    @JsonProperty("challenge_ts")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    var challengeTimestamp: OffsetDateTime = OffsetDateTime.now()

    var credit: Boolean? = null

    var hostname: String = ""

    @JsonProperty("error-codes")
    var errorCodes: List<String>? = null

    @JsonProperty("score_reason")
    var scoreReason: List<String>? = null
}