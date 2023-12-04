package com.labijie.application.identity.service.impl

import com.labijie.application.ApplicationRuntimeException
import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.configure
import com.labijie.application.exception.InvalidPhoneNumberException
import com.labijie.application.exception.OperationConcurrencyException
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.executeReadOnly
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.UserLoginTable
import com.labijie.application.identity.data.UserOpenIdTable
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.data.pojo.UserLogin
import com.labijie.application.identity.data.pojo.UserOpenId
import com.labijie.application.identity.data.pojo.dsl.UserDSL.selectOne
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.selectMany
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.selectOne
import com.labijie.application.identity.data.pojo.dsl.UserOpenIdDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserOpenIdDSL.selectOne
import com.labijie.application.identity.exception.LoginProviderKeyAlreadyExistedException
import com.labijie.application.identity.exception.UnsupportedLoginProviderException
import com.labijie.application.identity.exception.UserAlreadyExistedException
import com.labijie.application.identity.model.PlatformAccessToken
import com.labijie.application.identity.model.SocialRegisterInfo
import com.labijie.application.identity.model.SocialUserAndRoles
import com.labijie.application.identity.service.ISocialUserService
import com.labijie.application.identity.social.*
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.logger
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.crypto.password.PasswordEncoder
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
    passwordEncoder: PasswordEncoder,
    idGenerator: IIdGenerator,
    cacheManager: ICacheManager,
    transactionTemplate: TransactionTemplate
) : AbstractUserService(
    transactionTemplate,
    authServerProperties,
    idGenerator,
    passwordEncoder,
    cacheManager,
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

    protected val loginProviders: List<ILoginProvider> by lazy {
        this.context?.getBeansOfType(ILoginProvider::class.java)?.map {
            it.value
        } ?: listOf()
    }

    protected open fun getLoginProvider(loginProvider: String): ILoginProvider? {
        return loginProviders.firstOrNull { it.name.equals(loginProvider, true) }
    }

    protected fun getUserId(loginProvider: String, providerKey: String): Long? {
        return this.transactionTemplate.configure(isReadOnly = true).execute {
            val record = UserLoginTable.selectOne() {
                andWhere {
                    UserLoginTable.loginProvider.eq(loginProvider) and
                            UserLoginTable.providerKey.eq(providerKey)
                }
            }
            record?.userId
        }
    }

    override fun getSocialUser(loginProvider: String, authorizationCode: String): SocialUserAndRoles? {
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

    override fun addLoginProvider(userId: Long, loginProvider: String, authorizationCode: String): UserLogin {
        try {
            return this.transactionTemplate.execute {
                getUserById(userId) ?: throw UserNotFoundException()
                val r = fetchUserFromSocialCode(loginProvider, authorizationCode)
                this.addUserLogin(userId, loginProvider, r.token)
            }!!
        }catch (_: DuplicateKeyException){
            throw LoginProviderKeyAlreadyExistedException(loginProvider)
        }
    }

    override fun removeLoginProvider(userId: Long, loginProvider: String, authorizationCode: String?): Int {
        val token = authorizationCode?.let {
            fetchUserFromSocialCode(loginProvider, authorizationCode)
        }
        return this.transactionTemplate.execute {
            val list = UserLoginTable.selectMany {
                andWhere {
                    UserLoginTable.userId.eq(userId) and
                            UserLoginTable.loginProvider.eq(loginProvider)
                }
                token?.let {
                    andWhere { UserLoginTable.providerKey eq it.token.userKey }
                }
            }
            list.forEach {
                login->
                UserLoginTable.deleteWhere {
                    UserLoginTable.loginProvider.eq(login.loginProvider) and
                    UserLoginTable.providerKey.eq(login.providerKey)
                }
            }
            list.size
        } ?: 0
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
                val openId = UserOpenIdTable.selectOne {
                    andWhere { UserOpenIdTable.appId eq token.appId }
                    andWhere { UserOpenIdTable.loginProvider eq provider }
                    andWhere { UserOpenIdTable.userId eq userId }
                }
                if (openId == null) {
                    val userOpenId = UserOpenId().apply {
                        this.openId = token.appOpenId
                        this.appId = token.appId
                        this.loginProvider = provider
                        this.userId = userId
                    }
                    UserOpenIdTable.insert(userOpenId)
                }
            }
        } catch (e: DuplicateKeyException) {
            //忽略主键重复的异常，已经存在不做任何处理
        }
    }

    private fun addUserLogin(userId: Long, provider: String, token: PlatformAccessToken): UserLogin {
        return this.transactionTemplate.execute {
            val userLogin = UserLogin().apply {
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

            UserLoginTable.insert(userLogin)
            userLogin
        }!!
    }


    override fun registerSocialUser(
        socialRegisterInfo: SocialRegisterInfo,
        throwIfExisted: Boolean,
    ): SocialUserAndRoles {
        //解密用的向量为空，如果空认为电话号码为明文
        val iv = socialRegisterInfo.iv ?: ""

        val loginProvider = socialRegisterInfo.provider
        val r = fetchUserFromSocialCode(socialRegisterInfo.provider, socialRegisterInfo.code)
        val miniProvider = r.provider as? ILoginProviderPhoneNumberSupport
        if (r.userId != null && throwIfExisted) {
            throw UserAlreadyExistedException("user with login provider '${socialRegisterInfo.provider}' already existed")
        }

        var phoneNumber = socialRegisterInfo.phoneNumber

        if (miniProvider != null && iv.isNotBlank()) {
            phoneNumber = miniProvider.decryptPhoneNumber(socialRegisterInfo.phoneNumber, r.token, iv)
        }

        val valid =
            Pattern.matches(this.validationConfiguration.regex[ValidationProperties.PHONE_NUMBER]!!, phoneNumber)
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
            } ?: throw UserNotFoundException()
            val ctx = registrationContext
            if (ctx != null) {
                this.onSocialUserRegisteredAfterTranscationCommitted(ctx)
            }
            if (r.provider.isMultiOpenId) {
                //对于多 open id 的提供程，例如微信，第三方账号可能存在，但是仍然可能存在第三方的不同的 APP OPEN ID 不存在的问题, 单独事务处理
                addOpenIdIfNotExisted(userAndRoles.user.id, r.provider.name, r.token)
            }
            userAndRoles
        } catch (e: DuplicateRegisteringException) { //并发问题
            throw OperationConcurrencyException()
        }
        return SocialUserAndRoles(userAndRoles, loginProvider, r.token.userKey)
    }

    protected open fun onSocialUserRegisteredInTranscation(context: SocialUserRegistrationContext) {
        this.onUserRegisteredInTranscation(context.user, context.registerInfo.addition)
    }

    protected open fun onSocialUserRegisteredAfterTranscationCommitted(context: SocialUserRegistrationContext) {
        this.onUserRegisteredAfterTranscationCommitted(context.user, context.registerInfo.addition)
    }

    protected class DuplicateRegisteringException : ApplicationRuntimeException()

    override fun getOpenId(userId: Long, appId: String, loginProvider: String): String? {
        val provider = getLoginProvider(loginProvider) ?: throw UnsupportedLoginProviderException(loginProvider)
        return transactionTemplate.executeReadOnly {
            if(provider.isMultiOpenId) {
                val d = UserOpenIdTable.selectOne {
                    andWhere { UserLoginTable.userId eq userId }
                    andWhere{ UserOpenIdTable.appId eq  appId}
                    andWhere { UserOpenIdTable.loginProvider eq loginProvider }
                }
                d?.openId
            } else {
                UserLoginTable.selectOne {
                    andWhere { UserLoginTable.userId eq userId }
                    andWhere { UserLoginTable.loginProvider eq loginProvider }
                }?.providerKey
            }
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
        val user = if (phoneNumber.isBlank()) null else UserTable.selectOne {
            andWhere { UserTable.phoneNumber eq  phoneNumber.trim() }
        }
        if (user != null) {
            this.addUserLogin(user.id, loginProvider, token)
            val roles = this.getUserRoles(user.id)
            return Pair(SocialUserAndRoles(user, roles, provider.name, token.userKey), null)
        } else {
            try {
                val context =
                    UserGenerationContext(
                        socialRegisterInfo.username,
                        idGenerator,
                        loginProvider,
                        phoneNumber,
                        token
                    )
                val u =
                    socialSocialUserGenerator.generate(context, this.getDefaultUserType())
                val roles = this.getDefaultUserRoles()
                val userAndRoles = this.createUser(u, socialRegisterInfo.password, *roles)
                this.addUserLogin(userAndRoles.user.id, loginProvider, token)

                val reContext = SocialUserRegistrationContext(socialRegisterInfo, userAndRoles, provider, token)
                this.onSocialUserRegisteredInTranscation(reContext)
                return Pair(SocialUserAndRoles(userAndRoles, provider.name, token.userKey), reContext)
            } catch (e: DuplicateKeyException) {
                logger.warn("Duplicate social user register (provider: $loginProvider).", e)
                throw DuplicateRegisteringException()
            }
        }
    }
}