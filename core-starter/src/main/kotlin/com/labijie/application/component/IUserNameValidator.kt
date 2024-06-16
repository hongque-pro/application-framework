/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.component


interface IUserNameValidator {
    fun validate(username: String?, throwIfInvalid: Boolean): Boolean
}