package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-28
 */
class DataOwnerMissMatchedException(message:String? = null) :
    ErrorCodedException(ApplicationErrors.DataOwnerMissMatched, message)