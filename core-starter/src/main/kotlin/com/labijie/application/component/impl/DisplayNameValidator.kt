/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.component.impl

import com.labijie.application.component.IDisplayNameValidator
import com.labijie.application.exception.InvalidDisplayNameException
import java.util.regex.Pattern
import java.util.regex.Pattern.UNICODE_CHARACTER_CLASS


class DisplayNameValidator : IDisplayNameValidator {
    companion object {
        val pattern: Pattern by lazy {
            Pattern.compile("^(?=.{3,16}${'$'})(?![\\s\\._-])(?!.*([\\s\\._-]){2})[\\w\\p{L} -\\.]+(?<![\\.\\s_-])${'$'}", UNICODE_CHARACTER_CLASS)
        }
    }

    override fun validate(displayName: String?, throwIfInvalid: Boolean): Boolean {
        val valid = (!displayName.isNullOrBlank() && pattern.matcher(displayName).matches())

        if (!valid && throwIfInvalid) {
            throw InvalidDisplayNameException(inputDisplayName = displayName)
        }
        return valid
    }
}