package com.labijie.application.jackson

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.labijie.application.getEnumFromNumber
import com.labijie.application.getEnumFromString

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-15
 */
class DescribeEnumDeserializer(
    private val ctxType: JavaType? = null,
    private val property: BeanProperty? = null) : JsonDeserializer<Enum<*>>(),
    ContextualDeserializer {

    override fun createContextual(ctxt: DeserializationContext?, property: BeanProperty?): JsonDeserializer<*> {
        return DescribeEnumDeserializer(ctxt?.contextualType, property)
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Enum<*>? {
        val clazz =
            if (ctxt.contextualType != null) ctxt.contextualType.rawClass else this.property?.type?.rawClass ?: ctxType?.rawClass
        if(clazz == null) {
            return null
        }
        val txt = p.text?.trim() ?: ""
        val numberValue = txt.toIntOrNull()
        if (numberValue != null) {
            try {
                return getEnumFromNumber(clazz, numberValue) ?: throw JsonParseException(
                    p,
                    "Cant read json value '$numberValue' as enum (${clazz.simpleName})."
                )
            } catch (ex: NumberFormatException) {
                throw JsonParseException(p, "Cant read json value '$numberValue' as enum (${clazz.simpleName}).")
            }
        }

        if(txt.isNotBlank()) {
            val enum = getEnumFromString(clazz, txt, true)
            if (enum != null) {
                return enum
            }
        }
        return null
    }
}