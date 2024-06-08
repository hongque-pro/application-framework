/**
 * @author Anders Xiao
 * @date 2024-06-08
 */
package com.labijie.application.core.testing

import com.labijie.application.component.impl.UsernameValidator
import org.junit.jupiter.api.Assertions
import kotlin.test.Test


class UserNameValidatorTester {
    private val validator =  UsernameValidator()

    @Test
    fun goodUserName(){
       Assertions.assertTrue(validator.validate("endink", false))
        Assertions.assertTrue(validator.validate("a123456789012345", false))
        Assertions.assertTrue(validator.validate("a12345_678945", false))
        Assertions.assertTrue(validator.validate("a12345-a", false))
    }

    @Test
    fun badUserName(){
        Assertions.assertFalse(validator.validate("", false))
        Assertions.assertFalse(validator.validate("A bcfds", false))
        Assertions.assertFalse(validator.validate("_endink", false))
        Assertions.assertFalse(validator.validate("endink_", false))
        Assertions.assertFalse(validator.validate("a__accc", false))
        Assertions.assertFalse(validator.validate("a12-_345", false))
        Assertions.assertFalse(validator.validate("a12--345", false))
        Assertions.assertFalse(validator.validate("a12@345", false))
        Assertions.assertFalse(validator.validate("a12@345", false))
        Assertions.assertFalse(validator.validate("a1234567890123456", false))
        Assertions.assertFalse(validator.validate("abc", false))
    }
}