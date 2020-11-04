package com.labijie.application.aliyun.model

import com.labijie.application.aliyun.configuration.AfsSettings

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-24
 */
class AliyunCnfResponse {
    var oss: OssConfig = OssConfig()
    var afs: AfsSettings =
        AfsSettings()

    data class OssConfig(
        var private:BucketConfig = BucketConfig(),
        var public:BucketConfig = BucketConfig()
    )
}