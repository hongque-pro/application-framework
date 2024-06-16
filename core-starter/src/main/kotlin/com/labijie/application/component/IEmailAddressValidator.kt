/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.component


interface IEmailAddressValidator {
    fun validate(emailAddress: String?, throwIfInvalid: Boolean): Boolean
}