/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.component.impl

import com.labijie.application.component.IEmailAddressValidator
import com.labijie.application.exception.InvalidEmailException
import org.apache.commons.validator.routines.EmailValidator
import java.util.regex.Pattern


class EmailAddressValidator: IEmailAddressValidator {

    override fun validate(emailAddress: String, throwIfInvalid: Boolean): Boolean {
        val valid = emailAddress.isNotBlank() && EmailValidator.getInstance().isValid(emailAddress)

        if(!valid && throwIfInvalid){
            throw InvalidEmailException(inputEmail = emailAddress)
        }

        return valid
    }
}