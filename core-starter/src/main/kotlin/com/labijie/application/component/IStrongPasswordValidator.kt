/**
 * @author Anders Xiao
 * @date 2024-06-10
 */
package com.labijie.application.component


interface IStrongPasswordValidator {
    fun validate(password: String, throwIfInvalid: Boolean): Boolean
}