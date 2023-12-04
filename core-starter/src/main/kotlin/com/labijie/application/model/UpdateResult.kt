package com.labijie.application.model

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
data class UpdateResult<T>(var newValue: T? = null, var success:Boolean = true)