package com.labijie.application.identity.service

import com.labijie.application.identity.data.pojo.Role
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.model.RegisterBy
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.model.OrderBy

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
interface IUserService {
    fun getUserById(userId: Long): User?
    fun getUser(usr: String): User?
    fun existUser(userId: Long, throwIfNotExisted: Boolean = false): Boolean
    fun getUserRoles(userId: Long): List<Role>
    fun resetPassword(userId: Long, plainPassword: String): Boolean
    fun addRoleToUser(roleId: Long, userId: Long): Boolean
    fun removeRoleFromUser(roleId: Long, userId: Long): Boolean
    fun changePassword(
        userId: Long,
        oldPlainPassword: String,
        newPlainPassword: String
    ): Boolean

    fun changePhone(userId: Long, dialingCode: Short, phoneNumber: String, confirmed: Boolean = true): Boolean
    fun setUserEnabled(userId: Long, enabled: Boolean): Boolean
    fun getUsers(pageSize: Int, lastUserId: Long? = null, order: OrderBy = OrderBy.Descending): List<User>

    fun getOrCreateRole(roleName: String): Role
    fun createUser(user: User, plainPassword: String, vararg roles: String): UserAndRoles
    fun registerUser(register: RegisterInfo, by: RegisterBy = RegisterBy.Phone, customizer: ((user: User)->Unit)? = null): UserAndRoles


    fun updateUser(userId: Long, user: User): Boolean

    fun updateUserLastLogin(
        userId: Long,
        ipAddress: String = "0.0.0.0",
        platform: String? = null,
        area: String? = null,
        clientVersion: String? = null
    ): Boolean

    fun getDefaultUserType(): Byte {
        return 0
    }

    fun getDefaultUserRoles(): Array<String>
}