package com.labijie.application.hcaptcha

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

/**
 * @author Anders Xiao
 * @date 2025/7/29
 *
 * refer: https://docs.hcaptcha.com/#verify-the-user-response-server-side
 *
 * {
 *   "success" : true,
 *   "challenge_ts" : "2025-08-03T06:49:08.000000Z",
 *   "hostname" : "127.0.0.1",
 *   "credit" : false
 * }
 */
class HCapchaVerifyResponse {
    var success: Boolean = false

    @JsonProperty("challenge_ts")
    var challengeTimestamp: String? = null

    var credit: Boolean? = null

    var hostname: String? = null

    @JsonProperty("error-codes")
    var errorCodes: List<String>? = null

    @JsonProperty("score_reason")
    var scoreReason: List<String>? = null

    companion object {
        fun HCapchaVerifyResponse.getChallengeTime() : OffsetDateTime? {
            return challengeTimestamp?.let {
                try {
                    OffsetDateTime.parse(it)
                }catch (e: DateTimeParseException){
                    null
                }
            }
        }
    }
}