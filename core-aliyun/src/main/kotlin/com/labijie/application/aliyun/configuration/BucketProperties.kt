package com.labijie.application.aliyun.configuration

import java.net.URI

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-21
 */
data class BucketProperties (
    var internalEndPoint: URI?= null,

    var region: String = "",
    var bucket: String = "",
    var endpoint: URI? =  null,
    var customDomain:String = ""
)