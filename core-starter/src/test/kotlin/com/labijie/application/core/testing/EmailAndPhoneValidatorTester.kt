/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.core.testing

import com.labijie.application.component.impl.NationalPhoneValidator
import org.junit.jupiter.api.Assertions
import kotlin.test.Test


class EmailAndPhoneValidatorTester {

    private val phoneValidator by lazy {
        NationalPhoneValidator()
    }
    @Test
    fun testPhone() {
        Assertions.assertFalse(phoneValidator.validate(86, "138888", false))
        Assertions.assertTrue(phoneValidator.validate(86, "13777777777", false))
    }
}