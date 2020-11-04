package com.labijie.application

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 */
interface IDescribeEnum<TCode: Number> {
    val code:TCode
    val description:String
}