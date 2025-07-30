package com.labijie.application.auth.testing

import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.IdentityUtils
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.UserRoleTable
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.data.pojo.dsl.UserDSL.deleteByPrimaryKey
import com.labijie.application.identity.exception.InvalidPasswordException
import com.labijie.application.identity.exception.UnsupportedLoginProviderException
import com.labijie.application.identity.isEnabled
import com.labijie.application.identity.model.RegisterBy
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.model.SocialRegisterInfo
import com.labijie.application.identity.service.impl.DefaultUserService
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.impl.DebugIdGenerator
import com.labijie.infra.orm.test.ExposedTest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Author: Anders Xiao
 * Date: Created in 2020/6/3 17:28
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

@ExposedTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [UnitTestConfiguration::class])
//@AutoConfigureTestDatabase
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
open class DefaultUserServiceTester {

    companion object {
        const val IdentityTablePrefix = "identity_"
        const val defaultLoginProvider = "testProvider"
        const val notExistedLoginProvider = "testProvider2"
        const val defaultAuthorizationCode = "authorizationCode"
        const val defaultUserPassword = "!@#RFVC"
    }


    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var cacheManager: ICacheManager


    val snowflakeIdGenerator: IIdGenerator = DebugIdGenerator()
    lateinit var defaultUser: User
    val passwordEncoder = BCryptPasswordEncoder()

    private val svc by lazy {

        val identityProperties = IdentityProperties().apply {
            this.jdbcTablePrefix = IdentityTablePrefix
        }

        DefaultUserService(
            identityProperties,
            snowflakeIdGenerator,
            passwordEncoder,
            cacheManager,
            transactionTemplate
        )
    }

    @BeforeTest
    fun before() {
        defaultUser = IdentityUtils.createUser(1, "nick", 1)
        svc.createUser(defaultUser, defaultUserPassword)
        Assertions.assertNotNull(svc.getUserById(defaultUser.id), "create default user failed.")
    }

    @AfterTest
    fun after() {
        transactionTemplate.execute {
            UserTable.deleteByPrimaryKey(defaultUser.id)
            UserRoleTable.deleteWhere { userId.eq(defaultUser.id) }
        }

    }


    @Test
    fun getUserTester() {
        svc.getUser("test")
    }

    @Test
    fun createRole() {
        val u = IdentityUtils.createUser(this.snowflakeIdGenerator.newId(), "t1", 1)
        svc.createUser(u, "r1", "r2")
    }

    @Test
    fun getSocialUser() {
        Assertions.assertThrowsExactly(UnsupportedLoginProviderException::class.java) {
            svc.getSocialUser("test", "xxxxx")
        }
    }

    @Test
    fun addLoginProvider() {
        Assertions.assertThrowsExactly(UserNotFoundException::class.java) {
            svc.addLoginProvider(123, notExistedLoginProvider, defaultAuthorizationCode)
        }
    }

    @Test
    fun removeLoginProvider() {
        svc.removeLoginProvider(defaultUser.id, notExistedLoginProvider)
    }

//    @Test
//    fun registerSocialUser() {
//        Assertions.assertThrowsExactly(UnsupportedLoginProviderException::class.java) {
//            val reg = SocialRegisterInfo().apply {
//                this.username = "uab"
//                this.phoneNumber = "13888888889"
//                this.provider = "wechat"
//                this.password = "111111"
//                this.code = "#@#%#GFSVCST"
//            }
//            svc.registerSocialUser(reg, by = RegisterBy.Phone)
//        }
//    }
//
//    @Test
//    fun getOpenId() {
//        Assertions.assertThrowsExactly(UnsupportedLoginProviderException::class.java) {
//            svc.getOpenId(defaultUser.id, "tttttt", "wechat")
//        }
//    }

    @Test
    fun changePhone() {
        val newNumber = "13888888877"
        val oldNumber = defaultUser.phoneNumber
        var r = svc.changePhone(defaultUser.id, 86, newNumber)
        Assertions.assertTrue(r, "change phone nummber return false")
        val u = svc.getUser(newNumber)
        Assertions.assertNotNull(u, "find user failed after change phone number")

        r = svc.changePhone(defaultUser.id, 86, oldNumber)
        Assertions.assertTrue(r, "change phone nummber return false")
    }

    @Test
    fun registerUserByPhone() {
        val r = RegisterInfo(username = "newUsr1", password = "1232454564", phoneNumber = "13777777777")

        val u = svc.registerUser(r, RegisterBy.Phone)
        val u2 = svc.getUser(u.user.id.toString())

        Assertions.assertNotNull(u2)
        Assertions.assertEquals(u.user.phoneNumber, u2!!.phoneNumber)
        Assertions.assertTrue(u.user.phoneNumberConfirmed)
        Assertions.assertFalse(u.user.emailConfirmed)
    }

    @Test
    fun registerUserByEmail() {
        val r = RegisterInfo(username = "newUsr2", password = "1232454564", email = "13777777777@outlook.com")

        val u = svc.registerUser(r, RegisterBy.Email)
        val u2 = svc.getUser(u.user.id.toString())

        Assertions.assertNotNull(u2)
        Assertions.assertEquals(u.user.email, u2!!.email)
        Assertions.assertTrue(u.user.emailConfirmed)
        Assertions.assertFalse(u.user.phoneNumberConfirmed)
    }

    @Test
    fun getOrCreateRole() {
        val r = svc.getOrCreateRole("Role2")
        Assertions.assertNotNull(r)
    }

    @Test
    open fun setUserEnabled() {

        val u = svc.getUserById(defaultUser.id)!!

        Assertions.assertTrue(svc.setUserEnabled(u.id, !u.isEnabled()),"setUserEnabled return false")

        val u2 = svc.getUserById(defaultUser.id)!!
        Assertions.assertNotEquals(u.isEnabled(), u2.isEnabled())
        Assertions.assertNotEquals(u.lockoutEnabled, u2.lockoutEnabled)
    }
    @Test
    fun getUsers() {
        val users = svc.getUsers(10)
        Assertions.assertTrue(users.isNotEmpty())
    }

    @Test
    fun changePassword() {
        svc.changePassword(defaultUser.id, defaultUserPassword, "123243556")
        svc.changePassword(defaultUser.id, "123243556", "33333333")
        Assertions.assertThrowsExactly(InvalidPasswordException::class.java) {
            svc.changePassword(defaultUser.id, "44444444", "55555")
        }
    }

    @Test
    fun addRoleToUser() {
        val role = svc.getOrCreateRole("role1234")

        svc.addRoleToUser(role.id, defaultUser.id)
        val roles = svc.getUserRoles(defaultUser.id)
        Assertions.assertTrue(roles.any { it.id == role.id })
    }

    @Test
    fun removeRoleFromUser() {
        val role = svc.getOrCreateRole("role1234")
        svc.addRoleToUser(role.id, defaultUser.id)
        val roles = svc.getUserRoles(defaultUser.id)
        Assertions.assertTrue(roles.any { it.id == role.id })

        Assertions.assertTrue(svc.removeRoleFromUser(role.id, defaultUser.id))

        val roles2 = svc.getUserRoles(defaultUser.id)
        Assertions.assertFalse(roles2.any { it.id == role.id })
    }

    @Test
    fun resetPassword() {
        val pwd = "!QZCdrf@#%2dfdaf"
        svc.resetPassword(defaultUser.id, pwd)
        val u = svc.getUserById(defaultUser.id)
        passwordEncoder.matches(pwd, u?.passwordHash ?: "")
    }

    @Test
    fun updateUser() {
        svc.updateUser(defaultUser.id, defaultUser)
    }

    @Test
    fun updateUserLastLogin() {
        svc.updateUserLastLogin(defaultUser.id, ipAddress = "192.168.0.1")
        val u = svc.getUserById(defaultUser.id)

        Assertions.assertEquals(u?.lastSignInIp, "192.168.0.1")
    }

}
