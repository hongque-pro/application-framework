package com.labijie.application.open

import com.labijie.application.crypto.HashUtils
import com.labijie.application.open.exception.InvalidOpenApiSignatureException
import com.labijie.application.open.exception.UnsupportedSignAlgorithmException
import org.springframework.util.PatternMatchUtils
import java.lang.RuntimeException
import java.time.Duration

object OpenSignatureUtils {
    const val TimestampField = "timestamp"
    const val SerialField = "serial"
    const val AppIdField = "appid"

    fun generateKey(algorithm: String= "sha256"): String {
        return when (algorithm.lowercase()) {
            "sha256" -> HashUtils.genHmacSha256Key()
            "md5" -> HashUtils.genMD5Key()
            else -> throw UnsupportedSignAlgorithmException("Unsupported algorithm for open api signature.")
        }
    }

    fun verifySign(request: OpenApiRequest, key: String, algorithm: String = "sha256") {
        val (sign, data) = request.signAndData
        validateParameters(data)
        val valid = when (algorithm.lowercase()) {
            "sha256" -> HashUtils.verifyHmacSha256(data, sign, key)
            "md5" -> HashUtils.verifyMD5(data, sign, key)
            else -> throw UnsupportedSignAlgorithmException("Unsupported algorithm for open api signature.")
        }
        if (!valid) {
            throw InvalidOpenApiSignatureException()
        }
    }

    fun sign(request: OpenApiRequest, key: String, algorithm: String = "sha256"): String {
        val (_, data) = request.signAndData
        validateParameters(data)
        return when (algorithm.lowercase()) {
            "sha256" -> HashUtils.signHmacSha256(data, key)
            "md5" -> HashUtils.signMD5(data, key)
            else -> throw RuntimeException("Unsupported algorithm for open api signature.")
        }
    }

    fun validateParameters(data: Map<String, String>) {
        verifyAppId(data)
        verifyTimestamp(data)
        verifySerial(data)
    }

    private fun verifyAppId(data: Map<String, String>) {
        val appId = data.getOrDefault(AppIdField, "").trim()
        if (appId.isBlank() || !PatternMatchUtils.simpleMatch("^[0-9]+\\\$", appId)) {
            throw InvalidOpenApiSignatureException("$AppIdField must not be null, and must be a number.")
        }
    }

    private fun verifySerial(data: Map<String, String>) {
        val serial = data.getOrDefault(SerialField, "").trim()
        if (serial.isBlank() || !PatternMatchUtils.simpleMatch("^[A-Za-z0-9]+\\\$", serial) || serial.length > 32) {
            throw InvalidOpenApiSignatureException("$SerialField must not be null, and only letters and numbers less than 32 digits are allowed.")
        }
    }

    private fun verifyTimestamp(data: Map<String, String>) {
        val timeString = data.getOrDefault(TimestampField, "")
        if (timeString.isBlank()) {
            throw InvalidOpenApiSignatureException("$TimestampField missed.")
        }
        val time = timeString.toLongOrNull()
            ?: throw InvalidOpenApiSignatureException("$TimestampField is not a valid number format..")
        val now = System.currentTimeMillis()
        val minTime = now - Duration.ofMinutes(5).toMillis()
        val maxTime = now + Duration.ofMinutes(5).toMillis()

        if (time < minTime || time > maxTime) {
            throw InvalidOpenApiSignatureException("$TimestampField is not invalid value, check that the machine time is correct.")
        }
    }
}