package com.labijie.application.aliyun

import java.lang.RuntimeException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
class AliyunProcessException(message:String, cause:Throwable? = null) : RuntimeException(message, cause)