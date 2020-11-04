package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
class StoredObjectNotFoundException(message:String? = null)
    : ErrorCodedException(ApplicationErrors.StoredObjectNotFound, message ?: "Object not found in storage.")