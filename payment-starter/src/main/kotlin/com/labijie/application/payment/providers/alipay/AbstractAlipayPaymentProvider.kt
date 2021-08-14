package com.labijie.application.payment.providers.alipay

import com.labijie.application.exception.BadSignatureException
import com.labijie.application.exception.ThirdPartyExchangeException
import com.labijie.application.payment.*
import com.labijie.application.payment.abstraction.SceneSupportedPaymentProvider
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.exception.PaymentExchangeException
import com.labijie.application.payment.exception.PaymentSignatureException
import com.labijie.application.payment.providers.alipay.model.AlipayExchangeErrors
import com.labijie.application.payment.providers.alipay.model.AlipayTradeQueryReponse
import com.labijie.application.payment.providers.alipay.model.AlipayTradeStatus
import com.labijie.application.thridparty.alipay.AlipayResponseBase
import com.labijie.application.thridparty.alipay.AlipayUtilities
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.logger
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.validation.Valid
import kotlin.reflect.KClass

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 17:05
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
abstract class AbstractAlipayPaymentProvider(paymentProperties: PaymentProperties, options: AlipayPaymentOptions, restTemplates: MultiRestTemplates) :
    SceneSupportedPaymentProvider<AlipayPaymentOptions, IAlipayPaymentSceneSupport>(paymentProperties, options, restTemplates) {

    protected fun BigDecimal.toAmount(): String{
        val df = DecimalFormat("#.##")
        return df.format(this)
    }

    fun <T: AlipayResponseBase> requestApi(
        url:String,
        params: MutableMap<String, String>,
        responseType: KClass<T>,
        overrideOptions: AlipayPaymentOptions? = null): T {
        val usedOptions = overrideOptions ?: this.options
        try {
            return AlipayUtilities.requestAlipayApi(
                restTemplates.getRestTemplate(null, null),
                url,
                params,
                responseType,
                usedOptions.alipayPubKey
            )
        }catch (e: ThirdPartyExchangeException){
            throw PaymentExchangeException(this.name, e.message, e.cause, e.platformErrorCode)
        }
        catch (e: BadSignatureException){
            throw PaymentSignatureException(this.name, e.message, e.cause)
        }
    }

    protected fun combineBizContent(
        parameters: MutableMap<String, String>,
        bizContent: Map<String, Any>
    ) {
        parameters["biz_content"] = JacksonHelper.defaultObjectMapper.writeValueAsString(bizContent)
    }

    protected fun buildCommonsParams(
        method: String,
        appOAuthToken: String? = null,
        notifyUrl:String? = null,
        overrideOptions:AlipayPaymentOptions? = null
    ): MutableMap<String, String> {
        val usedOptions = overrideOptions ?: this.options

        val map = mutableMapOf(
            "app_id" to usedOptions.appId,
            "method" to method,
            "format" to "JSON",
            "charset" to "utf-8",
            "timestamp" to com.labijie.infra.utils.nowString(java.time.ZoneOffset.ofHours(8)),
            "version" to "1.0"
        )
        if (!appOAuthToken.isNullOrBlank()) {
            map["app_auth_token"] = appOAuthToken
        }
        if(!notifyUrl.isNullOrBlank()){
            map["notify_url"] = notifyUrl
        }
        return map
    }

    protected fun mapAlipayStatus(alipayTradeStatuts: String): PaymentTradeStatus {
        return when(alipayTradeStatuts){
            AlipayTradeStatus.WAIT_BUYER_PAY-> PaymentTradeStatus.WaitPay
            AlipayTradeStatus.TRADE_CLOSED -> PaymentTradeStatus.Close
            AlipayTradeStatus.TRADE_FINISHED,
            AlipayTradeStatus.TRADE_SUCCESS-> PaymentTradeStatus.Paid
            else -> {
                logger.warn("Unknown alipay trade status : $alipayTradeStatuts, and consider as WaitPay")
                PaymentTradeStatus.WaitPay
            }
        }
    }

    protected fun signatureData(parameters: MutableMap<String, String>, overrideOptions: AlipayPaymentOptions? = null) {
        val usedOptions  = overrideOptions ?: this.options

        parameters["sign_type"] = AlipayUtilities.SIGN_TYPE_RSA2
        val sign = AlipayUtilities.signature(parameters,usedOptions.appSecret, AlipayUtilities.SIGN_TYPE_RSA2)
        parameters["sign"] = sign
    }

    override fun queryTrade(@Valid query: PaymentTradeQuery): PaymentTradeQueryResult? {
        this.checkIsvParameter(query)
        //参考 ：https://docs.open.alipay.com/api_1/alipay.trade.query/
        val mntAuthCode = if (query.mode == TradeMode.ISV) query.platformMerchantKey else null
        val parameters = this.buildCommonsParams("alipay.trade.query", mntAuthCode)

        val idField = if(query.isPlatformTradeId) "trade_no" else "out_trade_no"
        val bizContent = mapOf(
            idField to query.id
        )
        combineBizContent(parameters, bizContent)
        signatureData(parameters)
        try {
            val response = this.requestApi(options.exchangeUrl, parameters, AlipayTradeQueryReponse::class)
            return PaymentTradeQueryResult(
                response.outTradeNo,
                response.tradeNo,
                response.buyerUserId,
                BigDecimal(response.totalAmount),
                mapAlipayStatus(response.tradeStatus),
                response.tradeStatus,
                0
            )
        }
        catch (e: PaymentExchangeException){
            if(e.platformErrorCode == AlipayExchangeErrors.QUERY_TRADE_NOT_EXIST){
                return null
            }
            throw e
        }
    }


    override fun queryRefund(query: RefundQuery): RefundResult? {
        throw NotImplementedError("Not yet implemented")
    }

    override fun refund(trade: RefundTrade): RefundResult {
        throw NotImplementedError("Not yet implemented")
    }

    override fun parserRefundCallbackRequest(callbackPayload: Map<String, String>): RefundResult {
        throw NotImplementedError("Not yet implemented")
    }
}