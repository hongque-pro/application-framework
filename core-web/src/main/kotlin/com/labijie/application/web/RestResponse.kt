package com.labijie.application.web

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
data class RestResponse<T>(
        var data: T? = null
)