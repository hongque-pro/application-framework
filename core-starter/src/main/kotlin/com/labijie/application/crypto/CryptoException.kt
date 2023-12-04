package com.labijie.application.crypto

import com.labijie.application.ApplicationRuntimeException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
open class CryptoException(message:String? = null, cause:Throwable? = null) : ApplicationRuntimeException(message, cause)