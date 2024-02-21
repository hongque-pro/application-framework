/**
 * @author Anders Xiao
 * @date 2024-02-03
 */
package com.labijie.application.core.testing

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.labijie.application.IDescribeEnum
import com.labijie.application.jackson.DescribeEnumDeserializer
import com.labijie.application.jackson.DescribeEnumSerializer
import com.labijie.infra.json.JacksonHelper
import org.junit.jupiter.api.Assertions
import kotlin.test.Test
import kotlin.test.assertTrue


class EnumSerializerTester {

    companion object {
        private val mapper = ObjectMapper().apply {
            val enumModule = com.fasterxml.jackson.databind.module.SimpleModule("eumn-module")
            enumModule.addDeserializer(Enum::class.java, DescribeEnumDeserializer())
            enumModule.addSerializer(Enum::class.java, DescribeEnumSerializer)
            this.registerModules(enumModule)
        }
    }

    enum class SimpleTestingEnum {
        One,
        Two,
        Three
    }

    enum class DescribedTestingEnum(override val code: Byte, override val description: String) : IDescribeEnum<Byte> {
        One(1, "11"),
        Two(2, "22"),
        Three(3, "33")
    }

    data class TestObject(
        var enumValue: SimpleTestingEnum = SimpleTestingEnum.Two,
        var stringValue: String = "12334",
        var isEnabled: Boolean = true
    )


    data class TestDescribedObject(
        var enumValue: DescribedTestingEnum = DescribedTestingEnum.One,
        var stringValue: String = "54545435"
    )

    data class NullableObject(
        var descEnumValue: DescribedTestingEnum? = DescribedTestingEnum.One,
        var enumValue: SimpleTestingEnum? = null,
        var descEnumNullValue: DescribedTestingEnum? = null
    )

    @Test
    fun testNullable() {
        val b = NullableObject()
        val json = mapper.writeValueAsString(b)
        println(json)

        val v = mapper.readValue(json, NullableObject::class.java)
        Assertions.assertEquals(b, v)
    }

    @Test
    fun testSimpleEnum() {
        val origin = TestObject(SimpleTestingEnum.Three)
        val str = mapper.writeValueAsString(origin)

        print(str)

        val obj = mapper.readValue(str, TestObject::class.java)
        Assertions.assertEquals(origin, obj)

        val test1 = """
            {"enumValue":"three","stringValue":"12334"}
        """.trimIndent()

        val test2 = """
            {"enumValue":"ONE","stringValue":"12334"}
        """.trimIndent()

        val test3 = """
            {"enumValue":2,"stringValue":"12334"}
        """.trimIndent()

        val obj2 = mapper.readValue(test1, TestObject::class.java)
        Assertions.assertEquals(origin, obj2)

        val obj3 = mapper.readValue(test2, TestObject::class.java)
        Assertions.assertNotEquals(origin, obj3)

        val obj4 = mapper.readValue(test3, TestObject::class.java)
        Assertions.assertEquals(origin, obj4)
    }

    @Test
    fun testDescribedEnum() {
        val origin = TestDescribedObject(DescribedTestingEnum.Three)
        val str = mapper.writeValueAsString(origin)

        print(str)

        val obj = mapper.readValue(str, TestDescribedObject::class.java)
        Assertions.assertEquals(origin, obj)

        val test1 = """
            {"enumValue":"three","stringValue":"54545435"}
        """.trimIndent()

        val test2 = """
            {"enumValue":"ONE","stringValue":"54545435"}
        """.trimIndent()

        val test3 = """
            {"enumValue":3,"stringValue":"54545435"}
        """.trimIndent()

        val obj2 = mapper.readValue(test1, TestDescribedObject::class.java)
        Assertions.assertEquals(origin, obj2)

        val obj3 = mapper.readValue(test2, TestDescribedObject::class.java)
        Assertions.assertNotEquals(origin, obj3)

        val obj4 = mapper.readValue(test3, TestDescribedObject::class.java)
        Assertions.assertEquals(origin, obj4)
    }

    @Test
    fun testDescribedEnumValue() {
        val t = DescribedTestingEnum.Two
        val json = mapper.writeValueAsString(t)

        print(json)

        val v1 = mapper.readValue("2", DescribedTestingEnum::class.java)
        val v2 = mapper.readValue("\"two\"", DescribedTestingEnum::class.java)

        Assertions.assertEquals(v1, DescribedTestingEnum.Two)
        Assertions.assertEquals(v2, DescribedTestingEnum.Two)
    }

    @Test
    fun jsTest() {
       val string = JacksonHelper.serializeAsString(TestObject())
        assertTrue { string.isNotBlank() }
    }
}