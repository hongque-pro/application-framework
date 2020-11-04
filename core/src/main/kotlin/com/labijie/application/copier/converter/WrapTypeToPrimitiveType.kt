package com.labijie.application.copier.converter

import com.labijie.application.*
import com.labijie.application.copier.ICopierConverter

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-08
 */
object WrapTypeToPrimitiveType : ICopierConverter {
    override fun isSupported(source: Class<*>, target: Class<*>): Boolean {
        return (source ==  JAVA_LONG && target == Long::class.java) ||
                (source ==  JAVA_DOUBLE && target == Double::class.java) ||
                (source ==  JAVA_FLOAT && target == Float::class.java) ||
                (source ==  JAVA_INT && target == Int::class.java) ||
                (source ==  JAVA_SHORT && target == Short::class.java) ||
                (source ==  JAVA_BYTE && target == Byte::class.java)
    }

    override fun convert(source: Any?, target: Class<*>): Any? {
        if(source == null){
            return when(target){
                Long::class.java-> -1L
                Double::class.java-> (-1).toDouble()
                Float::class.java->-1f
                Int::class.java->-1
                Short::class.java->(-1).toShort()
                Byte::class.java->(-1).toByte()
                else->source
            }
        }
        return source
    }
}