package com.labijie.application.auth.service

import com.labijie.application.auth.data.RoleRecord as Role
import com.labijie.application.auth.data.UserRecord as User
import com.labijie.application.auth.model.RegisterInfo
import com.labijie.application.auth.model.UserAndRoles
import com.labijie.application.model.OrderBy

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
interface IUserService {
    fun getUserById(userId: Long): User?
    fun getUser(usr: String): User?
    fun getUserRoles(userId: Long): List<Role>
    fun resetPassword(userId: Long, password: String): Boolean
    fun addRoleToUser(roleId: Long, userId: Long): Boolean
    fun removeRoleFromUser(roleId: Long, userId: Long): Boolean
    fun changePassword(userId: Long, oldPassword: String, newPassword: String): Boolean
    fun changePhone(userId: Long, phoneNumber: String, confirmed: Boolean = true): Boolean
    fun setUserEnabled(userId: Long, enabled: Boolean): Boolean
    fun getUsers(pageSize: Int, lastUserId: Long? = null, order: OrderBy = OrderBy.Descending): List<User>

    fun getOrCreateRole(roleName: String): Role
    fun createUser(user: User, vararg roles: String): UserAndRoles
    fun registerUser(register: RegisterInfo): UserAndRoles
    fun updateUser(userId: Long, user: User): Boolean
    fun getDefaultUserType(): Byte {
        return 0
    }

    fun getDefaultUserRoles(): Array<String>
}