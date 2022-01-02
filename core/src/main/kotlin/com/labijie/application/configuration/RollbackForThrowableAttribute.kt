package com.labijie.application.configuration

import org.springframework.transaction.interceptor.DefaultTransactionAttribute

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/1/2
 * @Description:
 */
internal class RollbackForThrowableAttribute: DefaultTransactionAttribute() {
    override fun rollbackOn(ex: Throwable): Boolean {
        return true
    }
}