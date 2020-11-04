package com.labijie.application.auth.model

import com.labijie.application.auth.data.RoleRecord as Role
import com.labijie.application.auth.data.UserRecord as User

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
open class UserAndRoles(var user: User, var roles:List<Role>) {
}