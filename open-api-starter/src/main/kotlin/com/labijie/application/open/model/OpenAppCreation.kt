package com.labijie.application.open.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.intellij.lang.annotations.Pattern

data class OpenAppCreation(
    @get: NotBlank
    @get:Length(min=3, max=32)
    @get:Pattern("^[\\u4e00-\\u9fa5_a-zA-Z0-9_-]+$")
    var displayName: String = ""
)