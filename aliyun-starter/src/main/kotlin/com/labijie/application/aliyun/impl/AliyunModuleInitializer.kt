package com.labijie.application.aliyun.impl

import com.labijie.application.IModuleInitializer
import com.labijie.infra.utils.logger

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
class AliyunModuleInitializer : IModuleInitializer {
    companion object{
        var humanCheckerEnabled:Boolean = false
        var messageSenderEnabled: Boolean = false
        var ossStorageEnabled:Boolean =false
    }

    fun initialize(){
        logger.info("""
            >>>  aliyun addin <<<
            human-checker: $humanCheckerEnabled
            message-sender:$messageSenderEnabled
            oss-storage: $ossStorageEnabled
        """.trimIndent())
    }
}