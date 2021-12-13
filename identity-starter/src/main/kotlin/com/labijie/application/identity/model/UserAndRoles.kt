package com.labijie.application.identity.model

import com.labijie.application.identity.data.RoleRecord as Role
import com.labijie.application.identity.data.UserRecord as User

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
open class UserAndRoles(var user: User, var roles: List<Role>) {
    fun hasRole(role: String): Boolean {
        if (role.isBlank()) {
            return false
        }
        return this.roles.any {
            it.name == role
        }
    }

    fun hasAnyOfRole(vararg roles: String): Boolean {
        return this.roles.any {
            roles.contains(it.name)
        }
    }
}
