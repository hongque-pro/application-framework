package com.labijie.application.dapr.testing

import com.labijie.application.dapr.testing.context.DaprTestContext
import com.labijie.application.dapr.testing.context.TestDaprSecretsStore
import com.labijie.application.ApplicationRuntimeException
import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.infra.utils.logger
import io.dapr.client.DaprClient
import io.dapr.exceptions.DaprException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test

/**
 * @author Anders Xiao
 * @date 2023-12-10
 */

@SpringBootTest
@ContextConfiguration(classes = [DaprTestContext::class])
class DaprSpringBootStarterTester {

    @Autowired
    private lateinit var daprClient: DaprClient

    @Autowired(required = false)
    private var daprProperties: DaprProperties = DaprProperties()

    @Test
    fun daprSecretsStore() {
        logger.info("get")
        try {
            val testStore = TestDaprSecretsStore(daprClient, daprProperties)
            testStore.getSecretFromDapr("awsparameterstore", "/prod/backend/oauth2_rsa_private_key")
        }catch (_: ApplicationRuntimeException) {

        }
        catch (dp: DaprException) {
            if(dp.errorCode != "UNAVAILABLE") {
                throw dp
            }
        }
    }
}