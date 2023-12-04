package com.labijie.application.identity.service.impl

import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.configure
import com.labijie.application.exception.OperationConcurrencyException
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.executeReadOnly
import com.labijie.application.identity.IdentityCacheKeys
import com.labijie.application.identity.IdentityCacheKeys.getUserCacheKey
import com.labijie.application.identity.IdentityUtils
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.RoleTable
import com.labijie.application.identity.data.UserRoleTable
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.data.pojo.Role
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.data.pojo.UserRole
import com.labijie.application.identity.data.pojo.dsl.RoleDSL.insert
import com.labijie.application.identity.data.pojo.dsl.RoleDSL.selectOne
import com.labijie.application.identity.data.pojo.dsl.RoleDSL.toRoleList
import com.labijie.application.identity.data.pojo.dsl.UserDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserDSL.selectByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserDSL.selectOne
import com.labijie.application.identity.data.pojo.dsl.UserDSL.toUserList
import com.labijie.application.identity.data.pojo.dsl.UserDSL.updateByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserRoleDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserRoleDSL.selectMany
import com.labijie.application.identity.exception.InvalidPasswordException
import com.labijie.application.identity.exception.PhoneAlreadyExistedException
import com.labijie.application.identity.exception.RoleNotFoundException
import com.labijie.application.identity.exception.UserAlreadyExistedException
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.OrderBy
import com.labijie.application.removeAfterTransactionCommit
import com.labijie.caching.ICacheManager
import com.labijie.caching.getOrSet
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.ifNullOrBlank
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.support.TransactionTemplate
import java.time.Clock
import java.time.Duration
import java.time.Instant


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractUserService(
    protected val transactionTemplate: TransactionTemplate,
    protected val authServerProperties: IdentityProperties,
    protected val idGenerator: IIdGenerator,
    protected val passwordEncoder: PasswordEncoder,
    protected val cacheManager: ICacheManager,
) : IUserService, ApplicationContextAware {

    protected var context: ApplicationContext? = null
        private set

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.context = applicationContext
    }

    private var validationConfig: ValidationProperties? = null

    protected var validationConfiguration: ValidationProperties
        get() {
            if (validationConfig == null) {
                validationConfig =
                    this.context?.getBeansOfType(ValidationProperties::class.java)?.values?.firstOrNull()
                        ?: ValidationProperties()
            }
            return validationConfig!!
        }
        set(value) {
            validationConfig = value
        }

    protected fun getUserAndRoles(
        phoneNumber: String,
        loginProvider: String? = null
    ): UserAndRoles {
        val user = UserTable.selectOne {
            UserTable.phoneNumber eq phoneNumber
        } ?: throw UserNotFoundException(
            if(loginProvider.isNullOrBlank()) "User was not existed." else "User was not existed (provider: $loginProvider)."
        )
        val roles = this.getUserRoles(user.id)
        return UserAndRoles(user, roles)
    }

    protected fun getUserAndRoles(
        userId: Long,
        loginProvider: String? = null
    ): UserAndRoles {
        val user = this.getUserById(userId) ?: throw UserNotFoundException(
            if(loginProvider.isNullOrBlank()) "User was not existed." else "User was not existed (provider: $loginProvider)."
        )
        val roles = this.getUserRoles(userId)
        return UserAndRoles(user, roles)
    }

    override fun changePhone(userId: Long, phoneNumber: String, confirmed: Boolean): Boolean {
        val u = User()
        u.id = userId
        u.phoneNumber = phoneNumber
        u.phoneNumberConfirmed = confirmed
        u.concurrencyStamp = ShortId.newId()

        return transactionTemplate.execute {
            cacheManager.removeAfterTransactionCommit("u:$userId", authServerProperties.cacheRegion)
            val count = UserTable.update({UserTable.id eq userId}) {
                it[UserTable.phoneNumber] = phoneNumber
            }
            count == 1
        } ?: false
    }

    protected open fun onUserRegisteredInTranscation(user: UserAndRoles, addition:String?) {}

    protected open fun onUserRegisteredAfterTranscationCommitted(user: UserAndRoles, addition:String?) {}

    override fun registerUser(register: RegisterInfo): UserAndRoles {
        val u = this.transactionTemplate.configure(isReadOnly = true).execute {
            val p = UserTable.selectOne {
                andWhere { UserTable.phoneNumber eq register.phoneNumber }
            }
            if (p != null) {
                throw PhoneAlreadyExistedException()
            }
            val id = idGenerator.newId()

            val user = IdentityUtils.createUser(
                idGenerator.newId(),
                register.username.ifNullOrBlank { "u${id}" },
                register.phoneNumber,
                getDefaultUserType()
            )
            val userAndRoles = this.createUser(user, register.password, *this.getDefaultUserRoles())
            this.onUserRegisteredInTranscation(userAndRoles, register.addition)
            userAndRoles
        }!!
        this.onUserRegisteredAfterTranscationCommitted(u, register.addition)
        return u
    }

    override fun createUser(user: User, plainPassword:String, vararg roles: String): UserAndRoles {
        user.passwordHash = passwordEncoder.encode(plainPassword)

        return transactionTemplate.execute {
            val rolesExisted = if (roles.isNotEmpty()) {
                val roleEntities = roles.map {
                    getOrCreateRole(it)
                }
                roleEntities.forEach {
                    val key = UserRole().apply {
                        this.userId = user.id
                        this.roleId = it.id
                    }
                    UserRoleTable.insert(key)
                }
                roleEntities
            } else {
                listOf()
            }
            try {
                UserTable.insert(user)
            } catch (e: DuplicateKeyException) {
                throw UserAlreadyExistedException()
            }
            UserAndRoles(user, rolesExisted)
        }!!
    }

    override fun existUser(userId: Long, throwIfNotExisted: Boolean): Boolean {
        val u = transactionTemplate.executeReadOnly {
            UserTable.selectOne(UserTable.id) {
                andWhere { UserTable.id eq userId }
            }
        }
        if(throwIfNotExisted && u == null){
            throw UserNotFoundException()
        }
        return u != null
    }

    override fun getOrCreateRole(roleName: String): Role {

        var role = transactionTemplate.executeReadOnly {
            RoleTable.selectOne {
                andWhere { RoleTable.name eq roleName }
            }
        }

        return if (role == null) {
            role = Role().apply {
                this.id = idGenerator.newId()
                this.concurrencyStamp = ShortId.newId()
                this.name = roleName
            }
            try {
                this.transactionTemplate.execute {
                    RoleTable.insert(role)
                }
                role
            } catch (e: DuplicateKeyException) {
                throw OperationConcurrencyException()
            }
        }else {
            role
        }
    }

    override fun setUserEnabled(userId: Long, enabled: Boolean): Boolean {

        existUser(userId, true)

        val cacheKey = getUserCacheKey(userId)

        val lock = if(enabled) Instant.now(Clock.systemUTC()).minusMillis(Duration.ofDays(1).toMillis()).toEpochMilli() else Long.MAX_VALUE

        val count = transactionTemplate.execute {
            cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)
            UserTable.updateByPrimaryKey(userId) {
                it[lockoutEnabled] = !enabled
                it[lockoutEnd] = lock
            }
        } ?: 0


        return count > 0
    }

    override fun getUserById(userId: Long): User? {
        if (userId <= 0) {
            return null
        }
        val key = getUserCacheKey(userId)
        return cacheManager.getOrSet(key, authServerProperties.cacheUserTimeout) {
            transactionTemplate.executeReadOnly {
                UserTable.selectByPrimaryKey(userId)
            }
        }
    }

    override fun getUsers(pageSize: Int, lastUserId: Long?, order: OrderBy): List<User> {
        if (pageSize <= 0) {
            return listOf()
        }

        return transactionTemplate.executeReadOnly {
            val query = UserTable.selectAll()

            if (lastUserId != null) {
                if (order == OrderBy.Ascending) {
                    query.andWhere { UserTable.id greater lastUserId }
                } else {
                    query.andWhere { UserTable.id less lastUserId }
                }
            }

            if (order == OrderBy.Ascending) {
                query.orderBy(UserTable.id to SortOrder.ASC)
            } else {
                query.orderBy(UserTable.id to SortOrder.DESC)
            }
            query.limit(pageSize, 0)

            query.toUserList()
        } ?: listOf()
    }


    override fun changePassword(userId: Long, oldPlainPassword: String, newPlainPassword: String): Boolean {

        val user = transactionTemplate.configure(isReadOnly = true).execute {
            UserTable.selectByPrimaryKey(userId)
        } ?: throw UserNotFoundException(userId)

        if (!passwordEncoder.matches(oldPlainPassword, user.passwordHash)) {
            throw InvalidPasswordException("The old password was incorrect.")
        }

        val passwordHash = passwordEncoder.encode(newPlainPassword)


        val cacheKey = getUserCacheKey(userId)
        return transactionTemplate.execute {
            cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)
            val count = UserTable.update({UserTable.id eq userId}) {
                it[UserTable.passwordHash] = passwordHash
            }
            count == 1
        } ?: false
    }

    override fun addRoleToUser(roleId: Long, userId: Long): Boolean {

        val r = transactionTemplate.execute {
            existUser(userId, true)

            RoleTable.selectOne(RoleTable.id) {
                andWhere { RoleTable.id eq roleId }
            } ?: throw RoleNotFoundException(roleId)

            val ur = UserRole().apply {
                this.roleId = roleId
                this.userId = userId
            }

            UserRoleTable.insert(ur).insertedCount
        } ?: 0

        return r == 1
    }

    override fun removeRoleFromUser(roleId: Long, userId: Long): Boolean {
        return transactionTemplate.execute {
            val count = UserRoleTable.deleteWhere {
                UserRoleTable.roleId.eq(roleId) and UserRoleTable.userId.eq(userId)
            }
            count == 1
        } ?: false
    }

    private fun fetchAllRoles(): List<Role> {
        val roles = cacheManager.getOrSet(
            authServerProperties.jdbcTablePrefix.plus(IdentityCacheKeys.ALL_ROLES),
            authServerProperties.cacheRegion,
            authServerProperties.cacheRoleTimeout
        ) { _ ->

            transactionTemplate.configure(isReadOnly = true).execute {
                val list = RoleTable.selectAll().orderBy(RoleTable.name).toRoleList()
                list.ifEmpty { null }
            }

        }
        return roles ?: listOf()
    }


    override fun resetPassword(userId: Long, plainPassword: String): Boolean {
        existUser(userId, true)

        val cacheKey = getUserCacheKey(userId)

        val passwordHash = passwordEncoder.encode(plainPassword)
        return transactionTemplate.execute {
            cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)
            val count = UserTable.update({UserTable.id eq  userId}) {
                it[UserTable.passwordHash] = passwordHash
            }
            count > 0
        } ?: false
    }

    override fun getUser(usr: String): User? {
        val u = this.transactionTemplate.configure(isReadOnly = true).execute {
            getUserByPrimaryField(usr)
        }
        if (u != null) {
            this.cacheManager.set(
                "u:${u.id}", u,
                authServerProperties.cacheUserTimeout.toMillis()
            )
        }
        return u
    }


    private fun getUserByPrimaryField(usr: String): User? {
        val id = usr.toLongOrNull()
        if(id != null){
            return UserTable.select {
                UserTable.id.eq(id) or
                UserTable.phoneNumber.eq(usr) or
                UserTable.userName.eq(usr) or
                UserTable.email.eq(usr)
            }.toUserList().firstOrNull()
        }

        return UserTable.select {
                    UserTable.phoneNumber.eq(usr) or
                    UserTable.userName.eq(usr) or
                    UserTable.email.eq(usr)
        }.toUserList().firstOrNull()
    }

    override fun getUserRoles(userId: Long): List<Role> {

        val roleIds = transactionTemplate.configure(isReadOnly = true).execute {
            UserRoleTable.selectMany(UserRoleTable.roleId) {
                andWhere { UserRoleTable.userId eq userId }
            }.map { it.roleId }
        } ?: listOf()

        val roles = this.fetchAllRoles()
        return roles.filter { roleIds.contains(it.id) }
    }

    override fun updateUser(userId: Long, user: User): Boolean {
        val cacheKey = getUserCacheKey(userId)
        return transactionTemplate.execute {
            cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)

            UserTable.updateByPrimaryKey(user) > 0
        } ?: false
    }

    override fun updateUserLastLogin(userId: Long, ipAddress: String, platform: String?, area: String?, clientVersion: String?): Boolean {
        val cacheKey = getUserCacheKey(userId)
        return transactionTemplate.execute {
            cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)

            val count = UserTable.update({ UserTable.id eq userId }) {
                it[lastSignInIp] = ipAddress
                it[lastSignInPlatform] = platform ?: ""
                it[lastSignInArea] = area ?: ""
                it[lastClientVersion] = clientVersion ?: ""
                it[timeLastLogin] = System.currentTimeMillis()
            }
            count > 0
        } ?: false
    }
}