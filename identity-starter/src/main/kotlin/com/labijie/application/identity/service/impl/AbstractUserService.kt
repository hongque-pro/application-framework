package com.labijie.application.identity.service.impl

import com.labijie.application.SpringContext
import com.labijie.application.component.IMessageSender
import com.labijie.application.configuration.ValidationConfiguration
import com.labijie.application.configure
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.DynamicTableSupport
import com.labijie.application.identity.IdentityCacheKeys
import com.labijie.application.identity.IdentityCacheKeys.getUserCacheKey
import com.labijie.application.identity.IdentityUtils
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.RoleRecord
import com.labijie.application.identity.data.UserRecord
import com.labijie.application.identity.data.UserRoleRecord
import com.labijie.application.identity.data.extensions.*
import com.labijie.application.identity.data.mapper.RoleDynamicSqlSupport.Role
import com.labijie.application.identity.data.mapper.RoleMapper
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User
import com.labijie.application.identity.data.mapper.UserMapper
import com.labijie.application.identity.data.mapper.UserRoleDynamicSqlSupport.UserRole
import com.labijie.application.identity.data.mapper.UserRoleMapper
import com.labijie.application.identity.exception.*
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import com.labijie.application.model.OrderBy
import com.labijie.application.removeAfterTransactionCommit
import com.labijie.caching.ICacheManager
import com.labijie.caching.getOrSet
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.ShortId
import org.mybatis.dynamic.sql.SqlBuilder.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.selectList
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.Clock
import java.time.Duration
import java.time.Instant


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
abstract class AbstractUserService constructor(
    protected val authServerProperties: IdentityProperties,
    protected val idGenerator: IIdGenerator,
    protected val messageSender: IMessageSender,
    protected val cacheManager: ICacheManager,
    protected val userMapper: UserMapper,
    protected val userRoleMapper: UserRoleMapper,
    protected val roleMapper: RoleMapper,
    protected val transactionTemplate: TransactionTemplate
) : IUserService, ApplicationContextAware {


    protected var context: ApplicationContext? = null
        private set

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.context = applicationContext
    }

    private var encoder: PasswordEncoder? = null

    protected var passwordEncoder: PasswordEncoder
        get() {
            if (encoder == null) {
                encoder = this.context?.getBeansOfType(PasswordEncoder::class.java)?.values?.firstOrNull()
                    ?: BCryptPasswordEncoder()
            }
            return encoder!!
        }
        set(value) {
            encoder = value
        }

    private var validationConfig: ValidationConfiguration? = null

    protected var validationConfiguration: ValidationConfiguration
        get() {
            if (validationConfig == null) {
                validationConfig =
                    this.context?.getBeansOfType(ValidationConfiguration::class.java)?.values?.firstOrNull()
                        ?: ValidationConfiguration()
            }
            return validationConfig!!
        }
        set(value) {
            validationConfig = value
        }

    protected fun getUserAndRoles(
        phoneNumber: String,
        loginProvider: String
    ): UserAndRoles {
        val user = userMapper.selectOne {
            where(User.phoneNumber, isEqualTo(phoneNumber))
        } ?: throw UserNotFoundException(
            "$loginProvider user was not existed."
        )
        val roles = this.getUserRoles(user.id ?: 0)
        return UserAndRoles(user, roles)
    }

    protected fun getUserAndRoles(
        userId: Long,
        loginProvider: String
    ): UserAndRoles {
        val user = this.getUserById(userId) ?: throw UserNotFoundException(
            "$loginProvider user was not existed."
        )
        val roles = this.getUserRoles(userId)
        return UserAndRoles(user, roles)
    }

    @Transactional
    override fun changePhone(userId: Long, phoneNumber: String, confirmed: Boolean): Boolean {
        val u = UserRecord()
        u.id = userId
        u.phoneNumber = phoneNumber
        u.phoneNumberConfirmed = confirmed
        u.concurrencyStamp = ShortId.newId()

        cacheManager.removeAfterTransactionCommit("u:$userId", authServerProperties.cacheRegion)
        val count = userMapper.updateByPrimaryKeySelective(u)
        return count == 1
    }

    protected open fun onRegisteringUser(user: UserAndRoles) {}

    protected open fun onRegisteredUser(user: UserAndRoles) {}

    override fun registerUser(register: RegisterInfo): UserAndRoles {
        val u = this.transactionTemplate.execute {
            val p = userMapper.selectOne {
                where(User.phoneNumber, isEqualTo(register.phoneNumber))
            }
            if (p != null) {
                throw PhoneAlreadyExistedException()
            }

            if (register.password != register.repeatPassword) {
                throw InvalidRepeatPasswordException()
            }
            messageSender.verifySmsCaptcha(
                register.captcha,
                register.captchaStamp,
                register.phoneNumber,
                throwIfMissMatched = !SpringContext.isDevelopment
            )
            val user = IdentityUtils.createUser(
                idGenerator.newId(),
                register.userName,
                register.phoneNumber,
                passwordEncoder.encode(register.password),
                getDefaultUserType()
            )
            val userAndRoles = this.createUser(user, *this.getDefaultUserRoles())
            this.onRegisteringUser(userAndRoles)
            userAndRoles
        }!!
        this.onRegisteredUser(u)
        return u
    }

    @Transactional
    override fun createUser(user: UserRecord, vararg roles: String): UserAndRoles {
        val rolesExisted = if (roles.isNotEmpty()) {
            val roleEntities = roles.map {
                getOrCreateRole(it)
            }
            roleEntities.forEach {
                val key = UserRoleRecord().apply {
                    this.userId = user.id
                    this.roleId = it.id
                }
                userRoleMapper.insert(key)
            }
            roleEntities
        } else {
            listOf()
        }
        try {
            userMapper.insert(user)
        } catch (e: DuplicateKeyException) {
            throw UserAlreadyExistedException()
        }
        return UserAndRoles(user, rolesExisted)
    }

    override fun getOrCreateRole(roleName: String): RoleRecord {

        val role = roleMapper.selectOne {
            where(Role.name, isEqualTo(roleName))
        }

        return if (role == null) {
            val r = RoleRecord().apply {
                this.id = idGenerator.newId()
                this.concurrencyStamp = ShortId.newId()
                this.name = roleName
            }
            try {
                this.transactionTemplate.execute {
                    roleMapper.insert(r)
                    r
                }!!
            } catch (e: DuplicateKeyException) {
                roleMapper.selectOne {
                    where(Role.name, isEqualTo(roleName))
                }?: throw RoleNotFoundException(roleName)
            }
        }else {
            role
        }
    }

    @Transactional
    override fun setUserEnabled(userId: Long, enabled: Boolean): Boolean {
        userMapper.selectByPrimaryKey(userId) ?: throw UserNotFoundException(
            userId
        )

        val lock = if(enabled) Instant.now(Clock.systemUTC()).plusMillis(Duration.ofDays(1).toMillis()).toEpochMilli() else Long.MAX_VALUE

        val cacheKey = getUserCacheKey(userId)
        cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)
        val count = userMapper.update {
            set(User.lockoutEnabled).equalTo(enabled)
            set(User.lockoutEnd).equalTo(lock)
                .where(User.id, isEqualTo(userId))
        }

        return count > 0
    }

    override fun getUserById(userId: Long): UserRecord? {
        if (userId <= 0) {
            return null
        }
        val key = getUserCacheKey(userId)
        return cacheManager.getOrSet(key, authServerProperties.cacheUserTimeout) {
            userMapper.selectOne {
                where(User.id, isEqualTo(userId))
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getUsers(pageSize: Int, lastUserId: Long?, order: OrderBy): List<UserRecord> {
        if (pageSize < 0) {
            return listOf()
        }

        return userMapper.select {
            if(lastUserId != null){
                if (order == OrderBy.Ascending) {
                    where(User.id, isGreaterThan(lastUserId))
                } else {
                    where(User.id, isLessThan(lastUserId))
                }
            }
            if (order == OrderBy.Ascending){
                this.orderBy(User.id)
            }else{
                this.orderBy(User.id.descending())
            }
            this.limit(pageSize.toLong())
        }
    }


    @Transactional
    override fun changePassword(userId: Long, oldPassword: String, newPassword: String): Boolean {
        val user = this.userMapper.selectByPrimaryKey(userId) ?: throw UserNotFoundException(
            userId
        )
        if (!passwordEncoder.matches(oldPassword, user.passwordHash)) {
            throw InvalidPasswordException("The old password was incorrect.")
        }

        val passwordHash = passwordEncoder.encode(newPassword)

        val cacheKey = getUserCacheKey(userId)
        cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)
        val count = userMapper.update {
            set(User.passwordHash).equalTo(passwordHash)
                .where(User.id, isEqualTo(userId))
        }
        return count == 1
    }

    @Transactional
    override fun addRoleToUser(roleId: Long, userId: Long): Boolean {

        userMapper.selectByPrimaryKey(userId) ?: throw UserNotFoundException(userId)
        roleMapper.selectByPrimaryKey(roleId) ?: throw RoleNotFoundException(roleId)

        val record = UserRoleRecord().apply {
            this.roleId = roleId
            this.userId = userId
        }

        val count = userRoleMapper.insert(record)

        return count > 0
    }

    @Transactional
    override fun removeRoleFromUser(roleId: Long, userId: Long): Boolean {
        val count = userRoleMapper.deleteByPrimaryKey(userId, roleId)
        return count == 1
    }

    private fun fetchAllRoles(): List<RoleRecord> {
        val roles = cacheManager.getOrSet(
            authServerProperties.jdbcTablePrefix.plus(IdentityCacheKeys.ALL_ROLES),
            authServerProperties.cacheRegion,
            authServerProperties.cacheRoleTimeout
        ) { _ ->

            transactionTemplate.execute {
                val roles = roleMapper.select {
                    orderBy(Role.name)
                }
                if (roles.isNotEmpty()) {
                    roles
                } else {
                    null
                }
            }

        }
        return roles ?: listOf()
    }


    @Transactional
    override fun resetPassword(userId: Long, password: String): Boolean {
        val u = userMapper.selectOne() {
            where(User.id, isEqualTo(userId))
        }
        if (u != null) {
            val passwordHash = passwordEncoder.encode(password)

            val cacheKey = getUserCacheKey(userId)
            cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)
            val count =  userMapper.update {
                set(User.passwordHash).equalTo(passwordHash)
                    .where(User.id, isEqualTo(userId))
            }
            return count > 0
        }
        return false
    }

    override fun getUser(usr: String): UserRecord? {
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


    private fun getUserByPrimaryField(usr: String): UserRecord? {
        val id = usr.toLongOrNull()
        if(id != null){
            return userMapper.selectOne {
                where(User.id, isEqualTo(id))
                    .or(User.phoneNumber, isEqualTo(usr))
                    .or(User.userName, isEqualTo(usr))
                    .or(User.email, isEqualTo(usr))
            }
        }
        return userMapper.selectOne {
            where(User.phoneNumber, isEqualTo(usr))
                .or(User.userName, isEqualTo(usr))
                .or(User.email, isEqualTo(usr))
        }
    }

    @Transactional(readOnly = true)
    override fun getUserRoles(userId: Long): List<RoleRecord> {

        val roleIds = selectList(userRoleMapper::selectMany, listOf(UserRole.roleId), DynamicTableSupport.getTable(UserRole)) {
            where(UserRole.userId, isEqualTo(userId))
        }.map { it.roleId ?: 0 }

        val roles = this.fetchAllRoles()
        return roles.filter { roleIds.contains(it.id) }
    }

    @Transactional
    override fun updateUser(userId: Long, user: UserRecord): Boolean {
        user.id = userId
        val cacheKey = getUserCacheKey(userId)
        cacheManager.removeAfterTransactionCommit(cacheKey, authServerProperties.cacheRegion)

        return userMapper.updateByPrimaryKeySelective(user) > 0
    }
}