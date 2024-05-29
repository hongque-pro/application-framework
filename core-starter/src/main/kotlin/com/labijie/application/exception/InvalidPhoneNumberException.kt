package com.labijie.application.exception

import com.labijie.application.ApplicationErrors
import com.labijie.application.ErrorCodedException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class InvalidPhoneNumberException(message:String? = null, val inputPhone: String)
    : ErrorCodedException(ApplicationErrors.InvalidPhoneNumber, message?:"bad phone number format") {}