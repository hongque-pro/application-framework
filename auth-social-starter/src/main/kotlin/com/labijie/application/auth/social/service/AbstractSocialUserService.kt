package com.labijie.application.auth.social.service

import com.labijie.application.ApplicationRuntimeException
import com.labijie.application.auth.configuration.AuthServerProperties
import com.labijie.application.auth.data.UserLoginRecord
import com.labijie.application.auth.data.UserOpenIdRecord
import com.labijie.application.auth.data.extensions.insert
import com.labijie.application.auth.data.extensions.selectOne
import com.labijie.application.auth.data.mapper.*
import com.labijie.application.auth.data.mapper.UserDynamicSqlSupport.User
import com.labijie.application.auth.data.mapper.UserLoginDynamicSqlSupport.UserLogin
import com.labijie.application.auth.data.mapper.UserOpenIdDynamicSqlSupport.UserOpenId
import com.labijie.application.auth.exception.UserAlreadyExistedException
import com.labijie.application.auth.model.UserAndRoles
import com.labijie.application.auth.service.impl.AbstractUserService
import com.labijie.application.auth.social.ILoginProvider
import com.labijie.application.auth.social.IMiniProgramProvider
import com.labijie.application.auth.social.component.DefaultSocialUserGenerator
import com.labijie.application.auth.social.component.ISocialUserGenerator
import com.labijie.application.auth.social.component.UserGenerationContext
import com.labijie.application.auth.social.exception.UnsupportedLoginProviderException
import com.labijie.application.auth.social.model.PlatformAccessToken
import com.labijie.application.auth.social.model.SocialRegisterInfo
import com.labijie.application.auth.social.model.SocialUserAndRoles
import com.labijie.application.component.IMessageSender
import com.labijie.application.configuration.ValidationConfiguration
import com.labijie.application.configure
import com.labijie.application.exception.InvalidPhoneNumberException
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.logger
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.insert
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.selectOne
import org.springframework.beans.factory.ObjectProvider
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.TransactionTemplate
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
abstract class AbstractSocialUserService(
    authServerProperties: AuthServerProperties,
    idGenerator: IIdGenerator,
    messageSender: IMessageSender,
    cacheManager: ICacheManager,
    userMapper: UserMapper,
    userRoleMapper: UserRoleMapper,
    roleMapper: RoleMapper,
    protected val userLoginMapper: UserLoginMapper,
    protected val userOpenIdMapper: UserOpenIdMapper,
    transactionTemplate: TransactionTemplate
) : AbstractUserService(
    authServerProperties,
    idGenerator,
    messageSender,
    cacheManager,
    userMapper,
    userRoleMapper,
    roleMapper,
    transactionTemplate
), ISocialUserService {


    private var generator: ISocialUserGenerator? = null

    protected var socialSocialUserGenerator: ISocialUserGenerator
        get() {
            if (generator == null) {
                generator = this.context?.getBeansOfType(ISocialUserGenerator::class.java)?.values?.firstOrNull()
                    ?: DefaultSocialUserGenerator()
            }
            return generator!!
        }
        set(value) {
            generator = value
        }

    protected var loginProviders: ObjectProvider<ILoginProvider>? = null
        get() {
            if (field == null) {
                field = this.context?.getBeanProvider(ILoginProvider::class.java)
            }
            return field!!
        }

    protected open fun getLoginProvider(loginProvider: String): ILoginProvider? {
        return loginProviders?.firstOrNull { it.name.equals(loginProvider, true) }
    }

    protected fun getUserId(loginProvider: String, providerKey: String): Long? {
        return this.transactionTemplate.execute {
            val record = userLoginMapper.selectOne {
                where(UserLogin.loginProvider, isEqualTo(loginProvider))
                    .and(UserLogin.providerKey, isEqualTo(providerKey))
            }
            record?.userId
        }
    }

    override fun signInSocialUser(loginProvider: String, authorizationCode: String): SocialUserAndRoles? {
        val result = this.fetchUserFromSocialCode(loginProvider, authorizationCode)
        val userId = result.userId
        if (userId != null) {
            val loginKey = result.token.userKey
            return transactionTemplate.configure(isReadOnly = true).execute {
                val user = this.getUserById(userId)
                if (user != null) {
                    val roles = this.getUserRoles(userId)
                    SocialUserAndRoles(user, roles, loginProvider, loginKey)
                } else {
                    null
                }
            }
        }
        return null
    }

    private data class ExchangeResult(val token: PlatformAccessToken, val userId: Long?, val provider: ILoginProvider)

    private fun fetchUserFromSocialCode(
        loginProvider: String,
        authorizationCode: String
    ): ExchangeResult {
        if (loginProvider.isBlank() || authorizationCode.isBlank()) {
            throw IllegalArgumentException("loginProvider or authorizationCode can not be null")
        }
        val mp = getLoginProvider(loginProvider)
            ?: throw UnsupportedLoginProviderException(loginProvider)
        val token = mp.exchangeToken(authorizationCode)
        val userId = this.getUserId(loginProvider, token.userKey)
        return ExchangeResult(token, userId, mp)
    }

    private fun addOpenIdIfNotExisted(userId: Long, provider: String, token: PlatformAccessToken) {
        try {
            this.transactionTemplate.configure(propagation = Propagation.REQUIRES_NEW).execute {

               val openId = userOpenIdMapper.selectOne{
                    where(UserOpenId.appId, isEqualTo(token.appId))
                        .and(UserOpenId.loginProvider, isEqualTo(provider))
                        .and(UserOpenId.userId, isEqualTo(userId))
                }
                if (openId == null) {
                    val userOpenId = UserOpenIdRecord().apply {
                        this.openId = token.appOpenId
                        this.appId = token.appId
                        this.loginProvider = provider
                        this.userId = userId
                    }
                    userOpenIdMapper.insert(userOpenId)
                }
            }
        } catch (e: DuplicateKeyException) {
            //忽略主键重复的异常，已经存在不做任何处理
        }
    }

    private fun addUserLogin(userId: Long, provider: String, token: PlatformAccessToken) {
        this.transactionTemplate.execute {
            val userLogin = UserLoginRecord().apply {
                this.userId = userId
                this.providerDisplayName = ""
                this.loginProvider = provider
                this.providerKey = token.userKey
            }

//            val userOpenId = UserOpenId().apply {
//                this.openId = token.appOpenId
//                this.appId = token.appId
//                this.loginProvider = provider
//                this.userId = userId
//            }
            //this.userOpenIdMapper.insert(userOpenId)

            userLoginMapper.insert(userLogin)
        }
    }


    override fun registerSocialUser(
        socialRegisterInfo: SocialRegisterInfo,
        throwIfExisted: Boolean
    ): SocialUserAndRoles {
        val loginProvider = socialRegisterInfo.provider
        val r = fetchUserFromSocialCode(socialRegisterInfo.provider, socialRegisterInfo.code)
        if (r.userId != null && throwIfExisted) {
            throw UserAlreadyExistedException()
        }
        val miniProvider = r.provider as? IMiniProgramProvider

        val phoneNumber =
            miniProvider?.decryptPhoneNumber(socialRegisterInfo.phoneNumber, r.token, socialRegisterInfo.iv)
                ?: socialRegisterInfo.phoneNumber

        val valid =
            Pattern.matches(this.validationConfiguration.regex[ValidationConfiguration.PHONE_NUMBER]!!, phoneNumber)
        if (!valid) {
            throw InvalidPhoneNumberException()
        }

        val userAndRoles = try {
            var registrationContext: SocialUserRegistrationContext? = null
            val userAndRoles = transactionTemplate.execute {
                if (r.userId != null) {
                    //存在第三方绑定，直接返回账号信息
                    getUserAndRoles(r.userId, loginProvider)
                } else {
                    //不存在创建绑定或者创建账号
                    val (u, context) = createNewSocialUserOrLogin(socialRegisterInfo, phoneNumber, r.provider, r.token)
                    registrationContext = context
                    u
                }
            }
            val ctx = registrationContext
            if (ctx != null) {
                this.onRegisteredSocialUser(ctx)
            }
            if (r.provider.isMultiOpenId) {
                //对于多 open id 的提供程，例如微信，第三方账号可能存在，但是仍然可能存在第三方的不同的 APP OPEN ID 不存在的问题, 单独事务处理
                addOpenIdIfNotExisted(userAndRoles!!.user.id!!, r.provider.name, r.token)
            }
            userAndRoles!!
        } catch (e: DuplicateRegisteringException) { //并发问题
            getUserAndRoles(phoneNumber, loginProvider)
        }
        return SocialUserAndRoles(userAndRoles, loginProvider, r.token.userKey)
    }

    protected open fun onRegisteringSocialUser(context: SocialUserRegistrationContext) {}

    protected open fun onRegisteredSocialUser(context: SocialUserRegistrationContext) {}

    protected class DuplicateRegisteringException : ApplicationRuntimeException()

    override fun getOpenId(userId: Long, appId: String, loginProvider: String): String? {
        val provider = getLoginProvider(loginProvider) ?: throw UnsupportedLoginProviderException(loginProvider)
        return if (provider.isMultiOpenId) {
            val d = userOpenIdMapper.selectOne {
                where(UserOpenId.userId, isEqualTo(userId))
                    .and(UserOpenId.appId, isEqualTo(appId))
                    .and(UserOpenId.loginProvider, isEqualTo(loginProvider))
            }
            d?.openId
        } else {
            userLoginMapper.selectOne {
                where(UserLogin.userId, isEqualTo(userId))
                    .and(UserLogin.loginProvider, isEqualTo(loginProvider))
            }?.providerKey
        }
    }

    private fun createNewSocialUserOrLogin(
        socialRegisterInfo: SocialRegisterInfo,
        phoneNumber: String,
        provider: ILoginProvider,
        token: PlatformAccessToken
    ): Pair<SocialUserAndRoles, SocialUserRegistrationContext?> {
        val loginProvider = provider.name

        //不存在第三方绑定，考虑手机号可能存在
        val user = if (phoneNumber.isBlank()) null else userMapper.selectOne { where(User.phoneNumber, isEqualTo(phoneNumber.trim())) }
        if (user != null) {
            this.addUserLogin(user.id!!, loginProvider, token)
            val roles = this.getUserRoles(user.id!!)
            return Pair(SocialUserAndRoles(user, roles, provider.name, token.userKey), null)
        } else {
            try {
                val context =
                    UserGenerationContext(this.passwordEncoder, idGenerator, loginProvider, phoneNumber, token)
                val u = socialSocialUserGenerator.generate(context, this.getDefaultUserType())
                val roles = this.getDefaultUserRoles()
                val userAndRoles = this.createUser(u, *roles)
                this.addUserLogin(userAndRoles.user.id!!, loginProvider, token)

                val reContext = SocialUserRegistrationContext(socialRegisterInfo, userAndRoles, provider, token)
                this.onRegisteringSocialUser(reContext)
                return Pair(SocialUserAndRoles(userAndRoles, provider.name, token.userKey), reContext)
            } catch (e: DuplicateKeyException) {
                logger.warn("Duplicate social user register (provider: $loginProvider).", e)
                throw DuplicateRegisteringException()
            }
        }
    }
}