package com.labijie.appliction.minio.model

import com.fasterxml.jackson.annotation.JsonProperty

class S3Policy(vararg buckets: String){
    @JsonProperty("Version")
    val version = "2012-10-17"

    @JsonProperty("Statement")
    var statement = arrayOf(S3PolicyStatement(*buckets))
}