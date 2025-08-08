/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.component.impl

import com.labijie.application.component.IUserNameValidator
import com.labijie.application.exception.InvalidUsernameException
import java.util.regex.Pattern


class UsernameValidator : IUserNameValidator {

    companion object {
        val pattern: Pattern by lazy {
            Pattern.compile("^(?=.{4,16}${'$'})(?![_-])(?!.*([_-]){2})[a-zA-Z0-9_-]+(?<![_-])${'$'}")
        }
    }

    override fun validate(username: String?, throwIfInvalid: Boolean): Boolean {
        if(username.isNullOrEmpty()) {
            return true
        }
        val valid = (!pattern.matcher(username).matches())

        if (!valid && throwIfInvalid) {
            throw InvalidUsernameException(inputUsername = username)
        }
        return valid
    }
}