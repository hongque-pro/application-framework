package com.labijie.application.aliyun.configuration


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
data class OssSettings(
    var useInternal:Boolean = true,
    var private: BucketProperties = BucketProperties(),
    var public: BucketProperties = BucketProperties()
)

