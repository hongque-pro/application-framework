package com.labijie.application.identity.component

import com.labijie.application.identity.data.pojo.User

/**
 * @author Anders Xiao
 * @date 2025/8/2
 */
interface ICustomUserDataPersistence {
    fun persistUser(user: User, registerInfo: Map<String, String>?): User
}