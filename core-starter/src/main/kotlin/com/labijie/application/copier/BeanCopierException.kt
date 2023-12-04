package com.labijie.application.copier

import com.labijie.infra.utils.ifNullOrBlank
import java.lang.RuntimeException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 */
class BeanCopierException(sourceType:Class<*>, targetType:Class<*>, contextType:Class<*>? = null)
    : RuntimeException("Cant convert from ${sourceType.simpleName} to ${targetType.simpleName} (context: ${contextType?.simpleName.ifNullOrBlank("NULL")})") {
}