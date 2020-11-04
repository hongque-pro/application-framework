package com.labijie.application

import java.lang.RuntimeException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-13
 */
open class ApplicationRuntimeException(message:String? = null, cause:Throwable? = null) : RuntimeException(message, cause) {
}