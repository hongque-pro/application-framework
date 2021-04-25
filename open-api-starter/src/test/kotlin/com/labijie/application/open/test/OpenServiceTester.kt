package com.labijie.application.open.test

import com.labijie.application.open.model.OpenAppCreation
import com.labijie.application.open.model.OpenPartnerCreation
import com.labijie.application.open.service.IOpenAppService
import com.labijie.application.open.service.IOpenPartnerService
import com.labijie.infra.impl.DebugIdGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import kotlin.test.BeforeTest
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@MybatisTest
@ContextConfiguration(classes = [UnitTestConfiguration::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("classpath:open.sql")
class OpenServiceTester {

    @Autowired
    private lateinit var openAppService: IOpenAppService

    @Autowired
    private lateinit var openPartnerService: IOpenPartnerService

    private var testPartnerId: Long = 0

    companion object{
        const val TestUserId = 11223344L
    }

    @BeforeTest
    fun createTestData(){
        val partnerCreation =OpenPartnerCreation().apply {
            this.contact = "test"
            this.email = "test@test.com"
            this.name = "test-u"
            this.phoneNumber="13888888888"
            this.timeExpired = Long.MAX_VALUE
        }
        testPartnerId = openPartnerService.createPartner(partnerCreation).id
    }

    @Test
    fun testCreateApp(){
        val creation = OpenAppCreation().apply {
            this.displayName = "测试"
        }
        val openApp = openAppService.createApp(creation, this.testPartnerId)
        Assertions.assertTrue(openApp.appId > 0)
    }


}