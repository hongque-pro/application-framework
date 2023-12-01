package com.labijie.application.component

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-24
 *
 * 人机验证实现
 */
interface IHumanChecker {
    fun check(token: String, clientIp: String): Boolean
}