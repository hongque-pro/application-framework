package com.labijie.application.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.labijie.application.IDescribeEnum
import java.io.IOException



/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-15
 */
object DescribeEnumSerializer : JsonSerializer<Enum<*>>() {
    override fun serialize(value: Enum<*>?, gen: JsonGenerator, serializers: SerializerProvider?) {
        val describer = value as? IDescribeEnum<*>
        if(value == null){
            gen.writeNull()
        }else if(describer != null) {
            gen.writeNumber(describer.code.toInt())
        }else{
            gen.writeNumber(value.ordinal)
        }
    }
}