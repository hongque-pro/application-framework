/**
 * @author Anders Xiao
 * @date 2024-02-21
 */
package com.labijie.application.dapr.components

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.MessageLite
import io.dapr.client.domain.CloudEvent
import io.dapr.serializer.DaprObjectSerializer
import io.dapr.utils.TypeRef
import java.io.IOException
import kotlin.ByteArray


class DaprJsonSerializer(private val objectMapper: ObjectMapper) : DaprObjectSerializer {
    @Throws(IOException::class)
    override fun serialize(data: Any?): ByteArray? {
        return if (data == null) {
            null
        } else if (data.javaClass == Void::class.java) {
            null
        } else if (data is ByteArray) {
            data
        } else {
            if (data is MessageLite) data.toByteArray() else objectMapper.writeValueAsBytes(data)
        }
    }

    override fun <T : Any?> deserialize(content: ByteArray?, p1: TypeRef<T>?): T {
        @Suppress("UNCHECKED_CAST")
        return this.deserialize(content, objectMapper.constructType(p1?.type)) as T
    }

    override fun getContentType(): String {
        return "application/json"
    }


    @Throws(IOException::class)
    private fun deserialize(content: ByteArray?, javaType: JavaType?): Any? {
        if (javaType != null && !javaType.isTypeOrSubTypeOf(Void::class.java)) {
            if (javaType.isPrimitive) {
                return deserializePrimitives(content, javaType)
            } else if (content == null) {
                return null
            } else if (javaType.hasRawClass(ByteArray::class.java)) {
                return content
            } else if (content.isEmpty()) {
                return null
            } else if (javaType.hasRawClass(CloudEvent::class.java)) {
                return CloudEvent.deserialize(content)
            } else {
                if (javaType.isTypeOrSubTypeOf(MessageLite::class.java)) {
                    try {
                        val method = javaType.rawClass.getDeclaredMethod(
                            "parseFrom",
                            ByteArray::class.java
                        )
                        return method.invoke(null as Any?, content)
                    } catch (_: NoSuchMethodException) {
                    } catch (e: Exception) {
                        throw IOException(e)
                    }
                }

                return objectMapper.readValue(content, javaType)
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun parseNode(content: ByteArray?): JsonNode {
        return objectMapper.readTree(content)
    }

    @Throws(IOException::class)
    private fun deserializePrimitives(content: ByteArray?, javaType: JavaType): Any? {
        val v = if (content != null && content.isNotEmpty()) {
            objectMapper.readValue(content, javaType)
        } else if (javaType.hasRawClass(Boolean::class.java)) {
            false
        } else if (javaType.hasRawClass(java.lang.Byte.TYPE)) {
            0
        } else if (javaType.hasRawClass(java.lang.Short.TYPE)) {
            0.toShort()
        } else if (javaType.hasRawClass(Integer.TYPE)) {
            0
        } else if (javaType.hasRawClass(java.lang.Long.TYPE)) {
            0L
        } else if (javaType.hasRawClass(java.lang.Float.TYPE)) {
            0.0f
        } else if (javaType.hasRawClass(java.lang.Double.TYPE)) {
            0.0
        } else {
            if (javaType.hasRawClass(Character.TYPE)) '\u0000' else null
        }

        return v
    }
}