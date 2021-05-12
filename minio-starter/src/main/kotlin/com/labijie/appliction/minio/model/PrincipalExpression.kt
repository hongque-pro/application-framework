package com.labijie.appliction.minio.model

import com.fasterxml.jackson.annotation.JsonProperty

class PrincipalExpression(
    @get:JsonProperty("AWS")
    var expression: List<String> = listOf("*")
)