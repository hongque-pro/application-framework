package com.labijie.application

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-14
 */
data class ForwardList<T>(var list:List<T> = listOf(), var forwardToken: String? = null)