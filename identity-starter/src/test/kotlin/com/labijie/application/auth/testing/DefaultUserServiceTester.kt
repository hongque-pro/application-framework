package com.labijie.application.auth.testing

import com.labijie.application.identity.DynamicTableSupport
import com.labijie.application.component.impl.NoneMessageSender
import com.labijie.application.identity.IdentityUtils
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.mapper.*
import com.labijie.application.identity.service.impl.DefaultUserService
import com.labijie.caching.ICacheManager
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.commons.snowflake.ISlotProvider
import com.labijie.infra.commons.snowflake.ISlotProviderFactory
import com.labijie.infra.commons.snowflake.SnowflakeIdGenerator
import com.labijie.infra.commons.snowflake.configuration.SnowflakeConfig
import com.labijie.infra.commons.snowflake.providers.StaticSlotProvider
import org.junit.jupiter.api.extension.ExtendWith
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.Test

/**
 * Author: Anders Xiao
 * Date: Created in 2020/6/3 17:28
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

@ExtendWith(SpringExtension::class)
@MybatisTest
@ContextConfiguration(classes = [UnitTestConfiguration::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("classpath:auth.sql")
class DefaultUserServiceTester {

    companion object {
        const val IdentityTablePrefix = "identity_"
    }

    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var roleMapper: RoleMapper

    @Autowired
    private lateinit var userRoleMapper: UserRoleMapper

    @Autowired
    private lateinit var userLoginMapper: UserLoginMapper

    @Autowired
    private lateinit var userOpenIdMapper: UserOpenIdMapper

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var cacheManager: ICacheManager


    val snowflakeIdGenerator: IIdGenerator by lazy {
        val config = SnowflakeConfig().apply {
            this.provider = "static"
            this.static.slot = 1
        }

        val fact: ISlotProviderFactory = object : ISlotProviderFactory {
            override fun createProvider(providerName: String): ISlotProvider {
                return StaticSlotProvider()
            }
        }

        SnowflakeIdGenerator(config, fact)
    }

    private fun createServiceInstance(): DefaultUserService {

        val identityProperties = IdentityProperties().apply {
            this.jdbcTablePrefix = IdentityTablePrefix
        }

        DynamicTableSupport.prefix = IdentityTablePrefix

        return DefaultUserService(
                identityProperties,
                snowflakeIdGenerator,
                NoneMessageSender(),
                cacheManager,
                userMapper,
                userRoleMapper,
                roleMapper,
                userLoginMapper,
                userOpenIdMapper,
                transactionTemplate)
    }

    @Test
    fun getUserTester() {
        val svc = createServiceInstance()
        svc.getUser("test")
    }

    @Test
    fun createRole() {
        val svc = createServiceInstance()
        val u = IdentityUtils.createUser(this.snowflakeIdGenerator.newId(), "t1", "18888888888", "dfdsfsdf", 1)
        svc.createUser(u, "r1", "r2")
    }
}