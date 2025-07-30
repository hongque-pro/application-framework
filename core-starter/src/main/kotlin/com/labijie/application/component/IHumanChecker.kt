package com.labijie.application.component

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-24
 *
 * 人机验证实现
 */
interface IHumanChecker {

    fun clientStampRequired(): Boolean

    fun check(token: String, clientStamp: String?, clientIp: String?): Boolean
}