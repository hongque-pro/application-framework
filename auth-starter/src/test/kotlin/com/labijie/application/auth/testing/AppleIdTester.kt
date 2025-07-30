package com.labijie.application.auth.testing

import com.labijie.application.auth.testing.context.DummyServerAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration

/**
 * @author Anders Xiao
 * @date 2025/7/14
 */
@ContextConfiguration(
    classes = [
        DummyServerAutoConfiguration::class,
    ]
)
@WebMvcTest
class AppleIdTester {

}