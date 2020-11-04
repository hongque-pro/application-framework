package com.labijie.application.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer
import com.labijie.application.IDescribeEnum
import com.labijie.application.getEnumFromNumber
import java.io.IOException
import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-15
 */
class DescribeEnumDeserializer(private val property: BeanProperty? = null) : JsonDeserializer<Enum<*>>(),
    ContextualDeserializer {

    override fun createContextual(ctxt: DeserializationContext?, property: BeanProperty?): JsonDeserializer<*> {
        return DescribeEnumDeserializer(property)
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Enum<*>? {
        val rawValue = p.text?.trim()?.toIntOrNull()
        if (rawValue != null) {
            try {
//                val valueString = rawValue.toString()
                val clazz =
                    if (ctxt.contextualType != null) ctxt.contextualType.rawClass else this.property!!.type.rawClass
                @Suppress("UNCHECKED_CAST")
                val enumClass = clazz as Class<out Enum<*>>
                return getEnumFromNumber(clazz, rawValue) ?: throw EnumConstantNotPresentException(enumClass, rawValue.toString())
            } catch (ex: NumberFormatException) {
                val clazz =
                    if (ctxt.contextualType != null) ctxt.contextualType.rawClass else this.property!!.type.rawClass
                throw IOException("Cant read json value '$rawValue' as enum (${clazz.simpleName}).")
            }
        }
        return null
    }
}