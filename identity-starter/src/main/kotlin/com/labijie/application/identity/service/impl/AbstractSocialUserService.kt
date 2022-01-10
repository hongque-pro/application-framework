package com.labijie.application.identity.service.impl

import com.labijie.application.ApplicationRuntimeException
import com.labijie.application.component.IMessageSender
import com.labijie.application.configuration.ValidationConfiguration
import com.labijie.application.configure
import com.labijie.application.exception.InvalidCaptchaException
import com.labijie.application.exception.InvalidPhoneNumberException
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.UserLoginRecord
import com.labijie.application.identity.data.UserOpenIdRecord
import com.labijie.application.identity.data.extensions.insert
import com.labijie.application.identity.data.extensions.selectOne
import com.labijie.application.identity.data.mapper.*
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User
import com.labijie.application.identity.data.mapper.UserLoginDynamicSqlSupport.UserLogin
import com.labijie.application.identity.data.mapper.UserOpenIdDynamicSqlSupport.UserOpenId
import com.labijie.application.identity.exception.UnsupportedLoginProviderException
import com.labijie.application.identity.exception.UserAlreadyExistedException
import com.labijie.application.identity.model.PlatformAccessToken
import com.labijie.application.identity.model.SocialRegisterInfo
import com.labijie.application.identity.model.SocialUserAndRoles
import com.labijie.application.identity.service.ISocialUserService
import com.labijie.application.identity.social.*
import com.labijie.application.verifySmsCaptcha
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.logger
import org.mybatis.dynamic.sql.SqlBuilder
import org.springframework.beans.factory.ObjectProvider
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.TransactionTemplate
import java.util.regex.Pattern


/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 13:59
 * @Description:
 */
abstract class AbstractSocialUserService(
    authServerProperties: IdentityProperties,
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
                where(UserLogin.loginProvider, SqlBuilder.isEqualTo(loginProvider))
                    .and(UserLogin.providerKey, SqlBuilder.isEqualTo(providerKey))
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

                val openId = userOpenIdMapper.selectOne {
                    where(UserOpenId.appId, SqlBuilder.isEqualTo(token.appId))
                        .and(UserOpenId.loginProvider, SqlBuilder.isEqualTo(provider))
                        .and(UserOpenId.userId, SqlBuilder.isEqualTo(userId))
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
        throwIfExisted: Boolean,
        validateSms: Boolean
    ): SocialUserAndRoles {
        //解密用的向量为空，如果空认为电话号码为明文
        val iv = socialRegisterInfo.iv ?: ""

        if (validateSms && iv.isBlank()) { //不使用微信提供的电话需要先验证短信验证
            val smsCaptcha = socialRegisterInfo.captcha
            if (smsCaptcha == null) {
                throw InvalidCaptchaException()
            } else {
                messageSender.verifySmsCaptcha(smsCaptcha, true)
            }
        }

        val loginProvider = socialRegisterInfo.provider
        val r = fetchUserFromSocialCode(socialRegisterInfo.provider, socialRegisterInfo.code)
        if (r.userId != null && throwIfExisted) {
            throw UserAlreadyExistedException("user with login provider '${socialRegisterInfo.provider}' already existed")
        }
        val miniProvider = r.provider as? ILoginProviderPhoneNumberSupport
        var phoneNumber = socialRegisterInfo.phoneNumber

        if (miniProvider != null && iv.isNotBlank()) {
            phoneNumber = miniProvider.decryptPhoneNumber(socialRegisterInfo.phoneNumber, r.token, iv)
        }

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
                where(UserOpenId.userId, SqlBuilder.isEqualTo(userId))
                    .and(UserOpenId.appId, SqlBuilder.isEqualTo(appId))
                    .and(UserOpenId.loginProvider, SqlBuilder.isEqualTo(loginProvider))
            }
            d?.openId
        } else {
            userLoginMapper.selectOne {
                where(UserLogin.userId, SqlBuilder.isEqualTo(userId))
                    .and(UserLogin.loginProvider, SqlBuilder.isEqualTo(loginProvider))
            }?.providerKey
        }
    }

    private fun createNewSocialUserOrLogin(
        socialRegisterInfo: SocialRegisterInfo,
        phoneNumber: String,
        provider: ILoginProvider,
        token: PlatformAccessToken,
    ): Pair<SocialUserAndRoles, SocialUserRegistrationContext?> {
        val loginProvider = provider.name

        //不存在第三方绑定，考虑手机号可能存在
        val user = if (phoneNumber.isBlank()) null else userMapper.selectOne {
            where(
                User.phoneNumber,
                SqlBuilder.isEqualTo(phoneNumber.trim())
            )
        }
        if (user != null) {
            this.addUserLogin(user.id!!, loginProvider, token)
            val roles = this.getUserRoles(user.id!!)
            return Pair(SocialUserAndRoles(user, roles, provider.name, token.userKey), null)
        } else {
            try {
                val context =
                    UserGenerationContext(
                        socialRegisterInfo.username,
                        socialRegisterInfo.password,
                        this.passwordEncoder,
                        idGenerator,
                        loginProvider,
                        phoneNumber,
                        token
                    )
                val u =
                    socialSocialUserGenerator.generate(context, this.getDefaultUserType())
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