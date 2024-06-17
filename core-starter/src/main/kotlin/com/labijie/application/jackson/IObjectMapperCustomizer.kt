/**
 * @author Anders Xiao
 * @date 2024-06-17
 */
package com.labijie.application.jackson

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Implement the interface as a spring bean, a global object mapper that can be added automatically
 */
interface IObjectMapperCustomizer {
    fun customize(objectMapper: ObjectMapper)
}