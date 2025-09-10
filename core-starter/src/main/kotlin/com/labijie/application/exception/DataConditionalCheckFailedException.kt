
/**
 * @author Anders Xiao
 * @date 2025/9/10
 */

package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

class DataConditionalCheckFailedException(message:String? = null, cause: Throwable? = null) : ErrorCodedException(ApplicationErrors.DataConditionalCheckFailed, message, cause)