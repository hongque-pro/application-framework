package com.labijie.application.payment.testing

import com.labijie.application.payment.IPaymentProvider
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_SMART_NULLS
import org.springframework.http.HttpMethod
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.test.Test

class PaymentCallbackMvcInterceptorTester{
    private val interceptor: TestedCallbackMvcInterceptor

    companion object{
        const val DEFAULT_URL = "https://localhost:8888"
    }

    init {
        val properties = PaymentProperties().apply {
            this.callbackBaseUrl = DEFAULT_URL
        }
        val mockWechat =Mockito.mock(IPaymentProvider::class.java).apply {
            Mockito.`when`(this.name).thenReturn(WechatPaymentOptions.ProviderName)
        }
        val mockAlipay = Mockito.mock(IPaymentProvider::class.java).apply {
            Mockito.`when`(this.name).thenReturn(AlipayPaymentOptions.ProviderName)
        }

        interceptor = TestedCallbackMvcInterceptor(
            properties,
            listOf(mockWechat, mockAlipay),
            listOf()
        )
    }

    fun mockRequest(state:String? = null, paymentProvider: String = WechatPaymentOptions.ProviderName, method: HttpMethod = HttpMethod.POST): HttpServletRequest {
        val request = Mockito.mock(HttpServletRequest::class.java, RETURNS_SMART_NULLS).apply {

            val stuffix = if(state.isNullOrBlank()) "" else "/$state"
            Mockito.`when`(this.requestURI).thenReturn("${PaymentProperties.getPaymentCallbackPath(paymentProvider)}$stuffix")
            Mockito.`when`(this.headerNames).thenReturn(Collections.emptyEnumeration())
            Mockito.`when`(this.method).thenReturn(method.name)
        }

        return request
    }

    fun mockResponse(): HttpServletResponse {
        val response = Mockito.mock(HttpServletResponse::class.java)

        return response
    }

    @AfterEach
    fun releaseContext(){
        this.interceptor.afterCompletion(mockRequest(), mockResponse(), Any(), null)
    }

    @Test
    fun prepareHandlingContextWithState(){
        val state = "112233445566"
        val request = mockRequest(state)
        val response = mockResponse()
        val result = this.interceptor.prepareHandlingContextForTesting(request)

        Assertions.assertNotNull(result)
        Assertions.assertNotNull(result?.paymentProvider)
        //校验支付提供程序
        Assertions.assertEquals(WechatPaymentOptions.ProviderName, result?.paymentProvider?.name)
        //检验版本号
        Assertions.assertEquals(result?.version, PaymentProperties.VERSION)
        //校验 state
        Assertions.assertEquals(result?.state, state)
    }

    @Test
    fun prepareHandlingContextWithoutState(){
        val request = mockRequest(null)
        val response = mockResponse()
        val result = this.interceptor.prepareHandlingContextForTesting(request)

        Assertions.assertNotNull(result)
        Assertions.assertNotNull(result?.paymentProvider)
        //校验支付提供程序
        Assertions.assertEquals(WechatPaymentOptions.ProviderName, result?.paymentProvider?.name)
        //检验版本号
        Assertions.assertEquals(result?.version, PaymentProperties.VERSION)
        //校验 state
        Assertions.assertNull(result?.state)
    }

    @Test
    fun testCanNotPreHandle(){
        val state = "112233445566"
        val request = mockRequest(state, paymentProvider = "undefined")
        val response = mockResponse()
        val result = this.interceptor.prepareHandlingContextForTesting(request)

        Assertions.assertNull(result)
    }
}