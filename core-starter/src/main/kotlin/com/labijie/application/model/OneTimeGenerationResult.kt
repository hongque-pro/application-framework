package com.labijie.application.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
data class OneTimeGenerationResult(
    @get:JsonProperty("ot_stamp")
    val otStamp: String = ""
)