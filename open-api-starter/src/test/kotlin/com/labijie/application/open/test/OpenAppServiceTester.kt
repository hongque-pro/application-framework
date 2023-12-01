package com.labijie.application.open.test

import com.labijie.application.open.model.OpenAppCreation
import com.labijie.application.open.model.OpenPartnerCreation
import com.labijie.application.open.service.IOpenAppService
import com.labijie.application.open.service.IOpenPartnerService
import com.labijie.infra.orm.test.ExposedTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.EnableTransactionManagement
import kotlin.test.BeforeTest

/**
 * @author Anders Xiao
 * @date 2023-11-30
 */
@ExtendWith(SpringExtension::class)
@ExposedTest
@EnableTransactionManagement
@ContextConfiguration(classes = [UnitTestConfiguration::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OpenAppServiceTester {

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
    fun createApp() {
        val creation = OpenAppCreation().apply {
            this.displayName = "测试"
        }
        val openApp = openAppService.createApp(creation, this.testPartnerId)
        Assertions.assertTrue(openApp.id > 0)
    }

    @Test
    fun getByJsApiKey() {
    }

    @Test
    fun getByAppId() {
    }

    @Test
    fun renewSecret() {
    }

    @Test
    fun getSecret() {
    }

    @Test
    fun setAppStatus() {
    }

    @Test
    fun setAppConfiguration() {
    }

    @Test
    fun listApps() {
    }

    @Test
    fun testListApps() {
    }
}