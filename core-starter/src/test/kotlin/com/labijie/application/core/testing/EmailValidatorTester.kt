/**
 * @author Anders Xiao
 * @date 2024-07-25
 */
package com.labijie.application.core.testing

import com.labijie.application.validation.EmailValidatorUtils
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class EmailValidatorTester {

    @Test
    fun test() {
        assetValidEmail("2222@qq.com")
        assetValidEmail("abdsfds@qq.com")

        assetInvalidEmail("email 163@.com.")
        assetInvalidEmail("")
        assetInvalidEmail("email163@.com.")
        assetInvalidEmail("email163@.com,")
        assetInvalidEmail("email163.com@")
        assetInvalidEmail("email163.@com.")
        assetInvalidEmail("email163")
        assetInvalidEmail("aaa@email163")
        assetInvalidEmail("@aaaemail163")
    }

    private fun assetValidEmail(email: String) = assertTrue(EmailValidatorUtils.getInstance().isValid(email), "$email must be an email")

    private fun assetInvalidEmail(email: String) = assertFalse(EmailValidatorUtils.getInstance().isValid(email), "$email can not be an email")
}