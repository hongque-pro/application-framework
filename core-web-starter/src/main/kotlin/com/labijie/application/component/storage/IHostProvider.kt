package com.labijie.application.component.storage

/**
 * @author Anders Xiao
 * @date 2025/8/4
 */
@FunctionalInterface
interface IHostProvider {
    fun getHost(): String
}