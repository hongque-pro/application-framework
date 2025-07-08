package com.labijie.application.captcha.base

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
enum class CaptchaType {
    UNKNOWN,
    TYPE_DEFAULT, // 字母数字混合
    TYPE_ONLY_NUMBER, // 纯数字
    TYPE_ONLY_CHAR, // 纯字母
    TYPE_ONLY_UPPER, // 纯大写字母
    TYPE_ONLY_LOWER, // 纯小写字母
    TYPE_NUM_AND_UPPER, // 数字大写字母
}