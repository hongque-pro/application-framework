package com.labijie.application.aliyun.model

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
data class StsAccessToken(
    var securityToken: String = "",
    var accessKeySecret: String = "",
    var accessKeyId: String="",
    var expiration: Long=0
) {

}