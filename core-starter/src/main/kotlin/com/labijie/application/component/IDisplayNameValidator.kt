/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.component


interface IDisplayNameValidator {
    fun validate(displayName: String?, throwIfInvalid: Boolean): Boolean
}