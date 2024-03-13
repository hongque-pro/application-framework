package com.labijie.application.doc

import com.labijie.application.configuration.ApplicationWebProperties
import com.labijie.application.JsonMode
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.stereotype.Component


/**
 * @author Anders Xiao
 * @date 2023-12-01
 */

@Component
class DocPropertyCustomizer(private val webProperties: ApplicationWebProperties) : PropertyCustomizer {

    override fun customize(property: Schema<*>, type: AnnotatedType): Schema<*> {
        if (DocUtils.isEnumType(type.type)) {
            return DocUtils.getEnumSchema(property, type.type)
        }
        if (webProperties.jsonMode == JsonMode.JAVASCRIPT && DocUtils.isStringNumberType(type.type)) {
            return DocUtils.getNumberSchema(property, type.type)
        }
        return property
    }
}