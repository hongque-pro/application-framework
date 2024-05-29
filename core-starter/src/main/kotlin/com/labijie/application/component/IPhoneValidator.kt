/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.component


interface IPhoneValidator {
    fun validate(dialingCode: Short, phoneNumber: String, throwIfInvalid: Boolean): Boolean
}