/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.model


data class CaptchaResponseData(
    val image: String,
    val stamp: String,
    val mime: String,
)