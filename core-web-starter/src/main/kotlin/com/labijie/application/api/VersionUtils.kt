/**
 * @author Anders Xiao
 * @date 2025/9/18
 */
package com.labijie.application.api

import java.util.regex.Pattern

internal object InnerUtils {
    private val VERSION_NUMBER_PATTERN: Pattern = Pattern.compile("^(\\d{1,2}\\.\\d{1,2}(?:\\.\\d{1,2})?)$")

    /**
     * 检查版本匹配是否复合(最大三个版本)
     */
    fun checkVersionNumber(version: String, targetMethodOrType: Any?) {
        require(matchVersionNumber(version)) {
            String.format(
                "Invalid version number: @ApiVersion(\"%s\") at %s",
                version,
                targetMethodOrType
            )
        }
    }

    /**
     * 判断是否满足最大3个版本号的匹配
     */
    fun matchVersionNumber(version: String): Boolean {
        return version.isNotEmpty() && VERSION_NUMBER_PATTERN.matcher(version).find()
    }
}