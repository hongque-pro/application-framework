package com.labijie.appliction.minio.model

import com.fasterxml.jackson.annotation.JsonProperty

class S3PolicyStatementWithPrincipal(buckets: List<String>) : S3PolicyStatement(buckets) {
    constructor(bucket: String) : this(listOf(bucket))

    @get: JsonProperty("Principal")
    var principal: PrincipalExpression = PrincipalExpression()
}