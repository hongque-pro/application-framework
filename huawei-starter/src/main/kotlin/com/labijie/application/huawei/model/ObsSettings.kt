package com.labijie.application.huawei.model

class ObsSettings(
    var bucketName: String = "bucket",
    var domain: String = "obs.cn-north-4.myhuaweicloud.com",
    var ak: String = "",
    var sk: String = "",
    var endPoint: String = "",
    var privateDir: String = "d0",
    var publicDir: String = "d1",
    var expireSeconds: Long = 900L
)