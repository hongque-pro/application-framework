/**
 * @author Anders Xiao
 * @date 2024-06-10
 */
package com.labijie.application.component.impl

import com.labijie.application.component.IStrongPasswordValidator
import com.labijie.application.exception.StrongPasswordViolationException
import java.util.regex.Pattern


class StrongPasswordValidator : IStrongPasswordValidator {

    companion object {
        val pattern: Pattern by lazy {
            Pattern.compile("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^\\da-zA-Z\\s]).{4,16}\$")
        }
    }
    override fun validate(password: String?, throwIfInvalid: Boolean): Boolean {
        val valid = (!password.isNullOrBlank() && pattern.matcher(password).matches())

        if (!valid && throwIfInvalid) {
            throw StrongPasswordViolationException(inputPassword = password)
        }
        return valid
    }

}