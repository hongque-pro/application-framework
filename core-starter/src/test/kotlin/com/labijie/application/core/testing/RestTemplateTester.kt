package com.labijie.application.core.testing

import com.labijie.application.core.testing.context.TestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.client.RestTemplate
import kotlin.test.Test

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestContext::class])
class RestTemplateTester {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Test
    fun request(){
        val html = restTemplate.getForEntity("https://bing.com", String::class.java)
        Assertions.assertNotNull(html)
        Assertions.assertNotEquals(html.statusCode.value(), 301, "redirect is not support")
        Assertions.assertNotEquals(html.statusCode.value(), 302, "redirect is not support")
    }
}