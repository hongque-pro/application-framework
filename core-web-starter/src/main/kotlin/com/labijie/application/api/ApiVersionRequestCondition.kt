/**
 * @author Anders Xiao
 * @date 2025/9/18
 */

package com.labijie.application.api

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.servlet.mvc.condition.RequestCondition
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class ApiVersionRequestCondition(private val apiVersion: ApiVersion) : RequestCondition<ApiVersionRequestCondition> {

    companion object {
        const val VERSION_HEADER_KEY = "x-api-version"
        val VERSION_PARAM_NAMES = listOf("v", "version")

        private val prefixedVersions = ConcurrentHashMap<String, Pattern>()

        private fun getVersionPattern(version: ApiVersion): Pattern {
            return prefixedVersions.getOrPut("${version.requestPathPrefix.trim()}/${version.version.trim()}")  {
                if(version.requestPathPrefix.isNotBlank()) {
                    Pattern.compile("^/${version.requestPathPrefix}/v(\\d{1,2}\\.\\d{1,2}(?:\\.\\d{1,2})?)($|/).*")
                }else {
                    Pattern.compile("^/v(\\d{1,2}\\.\\d{1,2}(?:\\.\\d{1,2})?)($|/).*")
                }
            }
        }

    }

    override fun combine(other: ApiVersionRequestCondition): ApiVersionRequestCondition {
        return ApiVersionRequestCondition(other.apiVersion)
    }

    private fun HttpServletRequest.getVersion(): String? {
        val request = this
        // 1. header (case-insensitive)
        val headerVersion = request.headerNames
            .asSequence()
            .firstOrNull { it.equals(VERSION_HEADER_KEY, ignoreCase = true) }
            ?.let { request.getHeader(it)?.trim() }
        if (!headerVersion.isNullOrEmpty()) return headerVersion.removePrefix("v")

        // 2. query (case-insensitive)
        val queryVersion = request.parameterMap.entries
            .firstOrNull { entry -> VERSION_PARAM_NAMES.any { it.equals(entry.key, ignoreCase = true) } }
            ?.value?.firstOrNull()?.trim()
        if (!queryVersion.isNullOrEmpty()) return queryVersion.removePrefix("v")

        // 3. URL 前缀
        val matcher = getVersionPattern(apiVersion).matcher(request.requestURI)
        if (matcher.matches()) {
            return matcher.group(1) // 提取 1.0 或 1.0.1
        }

        return null
    }

    override fun getMatchingCondition(request: HttpServletRequest): ApiVersionRequestCondition? {
        val version = request.getVersion()
        if(version.isNullOrBlank()) return null

        val match = version.trim().equals(apiVersion.version.lowercase(), ignoreCase = true)
        if (match) {
            return this
        }
        return null
    }

    override fun compareTo(
        other: ApiVersionRequestCondition,
        request: HttpServletRequest
    ): Int {
        return other.apiVersion.version.trim().compareTo(apiVersion.version.trim())
    }
}