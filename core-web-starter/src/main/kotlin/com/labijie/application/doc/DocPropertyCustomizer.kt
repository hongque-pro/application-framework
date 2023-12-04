package com.labijie.application.doc

import com.fasterxml.jackson.databind.JavaType
import com.labijie.application.IDescribeEnum
import com.labijie.application.JAVA_DOUBLE
import com.labijie.application.JAVA_LONG
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.stereotype.Component
import java.math.BigDecimal


/**
 * @author Anders Xiao
 * @date 2023-12-01
 */

@Component
class DocPropertyCustomizer : PropertyCustomizer {

    override fun customize(property: Schema<*>, type: AnnotatedType): Schema<*> {
        if (DocUtils.isEnumType(type.type)) {
            return DocUtils.getEnumSchema(property, type.type)
        }
        if (DocUtils.isStringNumberType(type.type)) {
            return DocUtils.getNumberSchema(property, type.type)
        }
        return property
    }
}