package com.labijie.appliction.minio.model

import com.fasterxml.jackson.annotation.JsonProperty

open class S3PolicyStatement(buckets: List<String>) {
//    @get:JsonProperty("Sid")
//    var sid: String = "Stmt1"

    @get:JsonProperty("Effect")
    var effect: String = S3Policy.POLICY_EFFECT_ALLOW

    @get:JsonProperty("Action")
    var action: List<String> = listOf("s3:GetObject", "s3:PutObject")

    @get:JsonProperty("resource")
    var resource: List<String> = buckets.map {
        "arn:aws:s3:::$it/*"
    }
}