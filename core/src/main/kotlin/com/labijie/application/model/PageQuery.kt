package com.labijie.application.model

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-25
 */
abstract class PageQuery(
    var pageSize:Int = 20,

    var forwardToken:String? = null,

    @field:JsonIgnore
    private val maxPageSize:Int = 50) {

    @get:JsonIgnore
    val pageSizeLimited
        get() = this.pageSize.coerceAtMost(50)
}