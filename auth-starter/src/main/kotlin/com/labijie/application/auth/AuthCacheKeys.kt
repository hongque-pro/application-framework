package com.labijie.application.auth

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
object AuthCacheKeys {
    /**
     * 所有系统角色
     */
    const val ALL_ROLES = "all_roles"
    const val ALL_CLIENT_DETAILS = "all_clients"

    fun getUserCacheKey(userId: Long): String {
        return "u:$userId"
    }
}