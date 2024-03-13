/**
 * @author Anders Xiao
 * @date 2024-03-13
 */
package com.labijie.application.dapr.testing

import com.labijie.application.dapr.components.DaprJsonSerializer
import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.application.toUTF8StringOrEmpty
import com.labijie.infra.json.JacksonHelper
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class DaprSerializerTester {

    public class NestedStruct {
        var stringValue = "SSSSS"
        var intValue = 333
    }

    public class TestStruct {
        var nested = NestedStruct()
        var intValue = 333
    }
    @Test
    fun objectSerialize() {
        val testStore = DaprJsonSerializer(JacksonHelper.defaultObjectMapper)
        val jsonString = testStore.serialize(TestStruct())

        assertNotNull(jsonString)
        assertTrue {
            val str = jsonString.toUTF8StringOrEmpty()
            str.startsWith("{")
            str.endsWith("}")
        }
    }
}