package com.labijie.application.open.model

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class OpenAppCreation(
    @get: NotBlank
    @get:Length(min=3, max=32)
    @get:Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_-]+$")
    var displayName: String = ""
)