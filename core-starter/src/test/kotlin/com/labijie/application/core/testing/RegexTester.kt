/**
 * @author Anders Xiao
 * @date 2024-06-08
 */
package com.labijie.application.core.testing

import com.labijie.application.component.impl.DisplayNameValidator
import com.labijie.application.component.impl.UsernameValidator
import org.junit.jupiter.api.Assertions
import kotlin.test.Test


class RegexTester {
    private val usernameValidator =  UsernameValidator()
    private val displayNameValidator =  DisplayNameValidator()

    @Test
    fun goodUserName(){
        Assertions.assertTrue(usernameValidator.validate("abcd", false))
        Assertions.assertTrue(usernameValidator.validate("endink", false))
        Assertions.assertTrue(usernameValidator.validate("a123456789012345", false))
        Assertions.assertTrue(usernameValidator.validate("a12345_678945", false))
        Assertions.assertTrue(usernameValidator.validate("a12345-a", false))
    }

    @Test
    fun badUserName(){
        Assertions.assertFalse(usernameValidator.validate("", false))
        Assertions.assertFalse(usernameValidator.validate("abc", false))
        Assertions.assertFalse(usernameValidator.validate("是12354", false))
        Assertions.assertFalse(usernameValidator.validate("다섯12354", false))
        Assertions.assertFalse(usernameValidator.validate("àçèéô", false))
        Assertions.assertFalse(usernameValidator.validate("A bcfds", false))
        Assertions.assertFalse(usernameValidator.validate("A\tbcfds", false))
        Assertions.assertFalse(usernameValidator.validate("_endink", false))
        Assertions.assertFalse(usernameValidator.validate("endink_", false))
        Assertions.assertFalse(usernameValidator.validate("a__accc", false))
        Assertions.assertFalse(usernameValidator.validate("a12-_345", false))
        Assertions.assertFalse(usernameValidator.validate("a12--345", false))
        Assertions.assertFalse(usernameValidator.validate("a12@345", false))
        Assertions.assertFalse(usernameValidator.validate("a12@345", false))
        Assertions.assertFalse(usernameValidator.validate("a1234567890123456", false))
    }

    @Test
    fun goodDisplayName(){
        Assertions.assertTrue(displayNameValidator.validate("abc", false), "abc not matched")
        Assertions.assertTrue(displayNameValidator.validate("a.bc", false), "abc not matched")
        Assertions.assertTrue(displayNameValidator.validate("一二三", false))
        Assertions.assertTrue(displayNameValidator.validate("endink", false))
        Assertions.assertTrue(displayNameValidator.validate("a123456789012345", false), "Length ${"a123456789012345".length} not match")
        Assertions.assertTrue(displayNameValidator.validate("a12345_678945", false))
        Assertions.assertTrue(displayNameValidator.validate("a12345-a", false))
        Assertions.assertTrue(displayNameValidator.validate("A bcfds", false))
        Assertions.assertTrue(displayNameValidator.validate("A bc fds", false))
        Assertions.assertTrue(displayNameValidator.validate("다섯1232", false))
        Assertions.assertTrue(displayNameValidator.validate("àçèéô", false))
    }

    @Test
    fun badDisplayName(){
        Assertions.assertFalse(displayNameValidator.validate("", false))
        Assertions.assertFalse(displayNameValidator.validate("ab", false))
        Assertions.assertFalse(displayNameValidator.validate("a..bc", false))
        Assertions.assertFalse(displayNameValidator.validate(".abc", false))
        Assertions.assertFalse(displayNameValidator.validate("abc.", false))
        Assertions.assertFalse(displayNameValidator.validate("A\tbcfds", false))
        Assertions.assertFalse(displayNameValidator.validate("_endink", false))
        Assertions.assertFalse(displayNameValidator.validate("A@bcfds", false))
        Assertions.assertFalse(displayNameValidator.validate("endink_", false))
        Assertions.assertFalse(displayNameValidator.validate(" endink", false))
        Assertions.assertFalse(displayNameValidator.validate("endink ", false))
        Assertions.assertFalse(displayNameValidator.validate("a _accc", false))
        Assertions.assertFalse(displayNameValidator.validate("a__accc", false))
        Assertions.assertFalse(displayNameValidator.validate("a12-_345", false))
        Assertions.assertFalse(displayNameValidator.validate("a12--345", false))
        Assertions.assertFalse(displayNameValidator.validate("a12@345", false))
        Assertions.assertFalse(displayNameValidator.validate("a12@345", false))
        Assertions.assertFalse(displayNameValidator.validate("a1234567890123456", false))
    }
}