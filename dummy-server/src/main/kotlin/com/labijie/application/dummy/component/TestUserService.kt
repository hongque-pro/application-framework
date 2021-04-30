package com.labijie.application.dummy.component

import com.labijie.application.identity.data.RoleRecord
import com.labijie.application.identity.data.UserRecord
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.OrderBy
import com.labijie.infra.utils.ShortId
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class TestUserService : IUserService, ApplicationContextAware {

    private lateinit var appContext:ApplicationContext

    private val passwordEncoder by lazy {
        appContext.getBean(PasswordEncoder::class.java)
    }

    override fun getUserById(userId: Long): UserRecord? {
        return UserRecord(123456, userName = "test")
    }

    override fun getUser(usr: String): UserRecord? {
        return UserRecord(123456, userName = "test", passwordHash = passwordEncoder.encode("123456"))
    }

    override fun getUserRoles(userId: Long): List<RoleRecord> {
        return listOf(RoleRecord(1, ShortId.newId(), "USER"))
    }

    override fun resetPassword(userId: Long, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun addRoleToUser(roleId: Long, userId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeRoleFromUser(roleId: Long, userId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun changePassword(userId: Long, oldPassword: String, newPassword: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun changePhone(userId: Long, phoneNumber: String, confirmed: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun setUserEnabled(userId: Long, enabled: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUsers(pageSize: Int, lastUserId: Long?, order: OrderBy): List<UserRecord> {
        TODO("Not yet implemented")
    }

    override fun getOrCreateRole(roleName: String): RoleRecord {
        TODO("Not yet implemented")
    }

    override fun createUser(user: UserRecord, vararg roles: String): UserAndRoles {
        TODO("Not yet implemented")
    }

    override fun registerUser(register: RegisterInfo): UserAndRoles {
        TODO("Not yet implemented")
    }

    override fun updateUser(userId: Long, user: UserRecord): Boolean {
        return true
    }

    override fun getDefaultUserRoles(): Array<String> {
        return arrayOf("USER")
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        appContext = applicationContext
    }
}