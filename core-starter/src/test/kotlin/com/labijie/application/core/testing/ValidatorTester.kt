/**
 * @author Anders Xiao
 * @date 2024-07-30
 */
package com.labijie.application.core.testing

import com.labijie.application.component.impl.DisplayNameValidator
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ValidatorTester {

    private val dnv = DisplayNameValidator()

    private fun testInvalidDisplayName(value: String) {
        assertFalse(dnv.validate(value, false), "'${value}' must invalid")
    }

    private fun testValidDisplayName(value: String) {
        assertTrue(dnv.validate(value, false), "'${value}' must valid")
    }


    @Test
    fun testValidDisplayName() {
        testValidDisplayName("afddfg3254641234")
        testValidDisplayName("afddfg3 54641234")
        testValidDisplayName("afddfg3 5464 234")
        testValidDisplayName("afddfg3 5464-234")
        testValidDisplayName("afddfg3-234")
        testValidDisplayName("afddfg3-2-3_4")
        testValidDisplayName("afddfg3.2_3-4")
    }

    @Test
    fun testInvalidDisplayName() {
        testInvalidDisplayName(".")
        testInvalidDisplayName("afddfg3254641234ab")
        testInvalidDisplayName("a")
        testInvalidDisplayName("ab")
        testInvalidDisplayName("afddfg3  4641234")
        testInvalidDisplayName("afddfg3-_234")
        testInvalidDisplayName("afddfg3@234")
        testInvalidDisplayName("afddfg3?234")
        testInvalidDisplayName("afddfg3 -234")
        testInvalidDisplayName("afddfg3-_234")
        testInvalidDisplayName("afddfg-")
        testInvalidDisplayName("afddfg3.2_3-4.")
        testInvalidDisplayName("@afddfg")
        testInvalidDisplayName("afddfg@")
        testInvalidDisplayName("-afddfg")
        testInvalidDisplayName("afddfg-")
        testInvalidDisplayName(".afddfg")
        testInvalidDisplayName("afddfg.")
        testInvalidDisplayName("_afddfg")
        testInvalidDisplayName("_afddfg_")
        testInvalidDisplayName("afd.@dfg")
        testInvalidDisplayName(" afddfg")
        testInvalidDisplayName("afddfg ")
    }
}