package com.labijie.appliction.minio

data class PresignedUrl(val url:String, val expireInSeconds: Int, val contentType: String)