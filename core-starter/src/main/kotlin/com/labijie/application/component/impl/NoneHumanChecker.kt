package com.labijie.application.component.impl

import com.labijie.application.component.IHumanChecker

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-28
 */
class NoneHumanChecker : IHumanChecker {

    override fun check(token: String, clientStamp: String?, clientIp: String?): Boolean {
        return true
    }

    override fun clientStampRequired(): Boolean {
        return false
    }
}