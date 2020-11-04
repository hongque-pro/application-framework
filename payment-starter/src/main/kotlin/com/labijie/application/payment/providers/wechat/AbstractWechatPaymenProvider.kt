package com.labijie.application.payment.providers.wechat

import com.fasterxml.jackson.core.type.TypeReference
import com.labijie.application.crypto.AesUtils
import com.labijie.application.exception.BadSignatureException
import com.labijie.application.exception.ThirdPartyExchangeException
import com.labijie.application.parseDateTime
import com.labijie.application.payment.*
import com.labijie.application.payment.abstraction.SceneSupportedPaymentProvider
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.exception.*
import com.labijie.application.payment.providers.wechat.model.WebchatTradeStatus
import com.labijie.application.payment.providers.wechat.model.WechatRefundQueryResponse
import com.labijie.application.payment.providers.wechat.model.WechatRefundResponse
import com.labijie.application.payment.providers.wechat.model.WechatTradeQueryResponse
import com.labijie.application.thridparty.wechat.WechatResponse
import com.labijie.application.thridparty.wechat.WechatUtilities
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import com.labijie.infra.utils.toLocalDateTime
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.http.MediaType
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import kotlin.reflect.KClass

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 16:14
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
abstract class AbstractWechatPaymenProvider(
    paymentProperties: PaymentProperties,
    networkConfig: NetworkConfig,
    options: WechatPaymentOptions,
    restTemplates: MultiRestTemplates
) :
    SceneSupportedPaymentProvider<WechatPaymentOptions, IWechatPaymentSceneSupport>(
        paymentProperties,
        options,
        restTemplates
    ) {


    protected val hostIPAddress = networkConfig.getIPAddress().ifNullOrBlank { "127.0.0.1" }

    protected fun <TResponse : WechatResponse> requestApi(
        url: String,
        parameters: MutableMap<String, String>,
        responseType: KClass<TResponse>,
        useClientCertificate: Boolean = false,
        overrideOptions: WechatPaymentOptions? = null
    ): TResponse {
        val usedOptions = overrideOptions ?: this.options

        if (useClientCertificate && usedOptions.httpClientCertificateFile.isNullOrBlank()) {
            throw PaymentException(
                usedOptions.providerName,
                "WechatPaymentOptions.httpClientCertificateFile can not be null or empty when use client certificate."
            )
        }

        val certificateFile = if (useClientCertificate) usedOptions.httpClientCertificateFile else null
        val password = if (useClientCertificate) usedOptions.httpClientCertificatePassword else null
        val template = restTemplates.getRestTemplate(certificateFile, password)
        try {
            return WechatUtilities.requestWechatApi(
                template,
                url,
                parameters,
                responseType,
                usedOptions.merchantKey
            )
        } catch (e: ThirdPartyExchangeException) {
            when (e.platformErrorCode) {
                WechatPaymentErrors.OUT_TRADE_NO_USED -> throw DuplexPaymentTradeException(this.name)
                else -> throw PaymentExchangeException(this.name, e.message, e.cause, e.platformErrorCode)
            }
        } catch (e: BadSignatureException) {
            throw PaymentSignatureException(this.name, e.message, e.cause)
        }
    }

    protected fun getWechatDateTime(epochMilli: Long): String {
        return Instant.ofEpochMilli(epochMilli)
            .toLocalDateTime(ZoneOffset.ofHours(8))
            .format(WechatUtilities.DATETIME_FORMAT)
    }

    protected fun signatureData(
        data: MutableMap<String, String>,
        signAlias: String = "sign",
        signTypeAlias: String = "sign_type",
        overrideOptions: WechatPaymentOptions? = null,
        joinSignType: Boolean = true
    ) {
        val usedOptions = overrideOptions ?: this.options

        val aliasType = signTypeAlias.ifBlank { "sign_type" }
        val aliasSign = signAlias.ifBlank { "sign" }
        if (joinSignType) {
            data[aliasType] = WechatUtilities.SIGN_TYPE_HMAC_SHA256
        }
        val sign = WechatUtilities.signature(data, usedOptions.merchantKey, WechatUtilities.SIGN_TYPE_HMAC_SHA256)
        data[aliasSign] = sign
    }

    protected fun mapTradeState(status: String): PaymentTradeStatus {

        return when (status) {
            WebchatTradeStatus.NOTPAY,
            WebchatTradeStatus.CLOSED,
            WebchatTradeStatus.PAYERROR,
            WebchatTradeStatus.REVOKED -> PaymentTradeStatus.Close

            WebchatTradeStatus.REFUND,
            WebchatTradeStatus.SUCCESS -> PaymentTradeStatus.Paid

            WebchatTradeStatus.USERPAYING -> PaymentTradeStatus.WaitPay

            else -> {
                logger.warn("Unknown wechat trade status : $status, and consider as WaitPay")
                PaymentTradeStatus.WaitPay
            }
        }
    }

    protected fun buildQueryParameters(query: PaymentTradeQuery): MutableMap<String, String> {
        this.checkIsvParameter(query)

        val idField = if (query.isPlatformTradeId) "transaction_id" else "out_trade_no"

        val queryParams = mutableMapOf(
            idField to query.id,
            "appid" to options.appId,
            "mch_id" to options.appAccount,
            "nonce_str" to ShortId.newId()
        )
        if (query.mode == TradeMode.ISV) {
            //服务商模式，不关心 appid,因为 appid 只能是公众号，真实的小程序 id 配置在 subAppId
            queryParams["sub_appid"] = options.subAppId
            queryParams["sub_mch_id"] = query.platformMerchantKey
        }

        signatureData(queryParams)
        return queryParams
    }

    protected fun <TResponse : WechatResponse> queryTradeCore(
        url: String,
        query: PaymentTradeQuery,
        responseType: KClass<TResponse>,
        mapResponse: (response: TResponse) -> PaymentTradeQueryResult
    ): PaymentTradeQueryResult? {
        this.checkIsvParameter(query)
        val queryParams = buildQueryParameters(query)
        return try {
            val response = this.requestApi(
                url,
                queryParams,
                responseType
            )

            return mapResponse(response)
        } catch (e: PaymentExchangeException) {
            if (e.platformErrorCode == WechatPaymentErrors.QUERY_ORDERNOTEXIST) {
                null
            } else {
                throw e
            }
        }
    }

    protected fun mapTradeQueryResponse(
        response: WechatTradeQueryResponse,
        tradeMode: TradeMode
    ): PaymentTradeQueryResult {
        val openId = when (tradeMode) {
            TradeMode.Merchant -> response.openId
            TradeMode.ISV -> response.subOpenid
        }

        return PaymentTradeQueryResult(
            response.outTradeNo,
            response.transactionId,
            openId,
            BigDecimal(response.totalFee.let { if (it.isEmpty()) "0" else it }).divide(
                BigDecimal("100"),
                2,
                RoundingMode.DOWN
            ),
            mapTradeState(response.tradeState),
            response.tradeState,
            if (response.timeEnd.isEmpty()) 0L else parseDateTime(response.timeEnd, "yyyyMMddHHmmss")
        )
    }

    override fun parseCallbackResponse(isSuccess: Boolean, errorMessage: String?): CallbackResponse {
        var error = errorMessage
        if (!isSuccess && errorMessage != null && errorMessage.length > 127) {
            log.warn("Wechat callback handling: the length of the error message must be less than 128")
            error = errorMessage.substring(0, endIndex = 127)
        }
        val data = mutableMapOf(
            "return_code" to (if (isSuccess) "SUCCESS" else "FAIL"),
            "return_msg" to error.ifNullOrBlank { "callback handling fault" }
        )

        val body = WechatUtilities.buildXmlBody(data)
        return CallbackResponse(body, MediaType.APPLICATION_XML)
    }

    override fun parsePaymentCallbackRequest(callbackPayload: Map<String, String>): PaymentCallbackRequest {
        //回调解析，参考：https://pay.weixin.qq.com/wiki/doc/api/jsapi_sl.php?chapter=9_7

        val returnCode = callbackPayload["return_code"]
        val resultCode = callbackPayload["result_code"]

        if (returnCode != WechatResponse.RETURN_CODE_SUCCESS) {
            throw PaymentCallbackException(
                options.providerName,
                "Wechat callback with error result ( return_code: $returnCode, result_code: $resultCode ) ."
            )
        }

        val tables = mutableMapOf(*callbackPayload.toList().toTypedArray())
        val sign = tables.remove("sign") ?: throw PaymenCallbackFieldMissException(options.providerName, "sign")
        val signType = tables["sign_type"].ifNullOrBlank { WechatUtilities.SIGN_TYPE_HMAC_SHA256 }
        //验签
        if (!WechatUtilities.verifySignature(sign, options.merchantKey, tables, signType)) {
            throw PaymentSignatureException(options.providerName)
        }

        val appid = tables["sub_appid"].ifNullOrBlank(tables["appid"]).ifNullOrBlank { options.appId }
        val tradeId =
            tables["out_trade_no"] ?: throw PaymenCallbackFieldMissException(options.providerName, "out_trade_no")
        val platformTradeId =
            tables["transaction_id"] ?: throw PaymenCallbackFieldMissException(options.providerName, "transaction_id")
        val amount = tables["total_fee"] ?: throw PaymenCallbackFieldMissException(options.providerName, "total_fee")
        val timePaid = tables["time_end"] ?: throw PaymenCallbackFieldMissException(options.providerName, "time_end")
        val buyerId = tables["sub_openid"].ifNullOrBlank(tables["openid"])
        val state = tables["trade_state"]
            ?: if (resultCode == WechatResponse.RESULT_CODE_SUCCESS) WebchatTradeStatus.SUCCESS else WebchatTradeStatus.NOTPAY

        return PaymentCallbackRequest(
            appid,
            tradeId,
            platformTradeId,
            this.mapTradeState(state),
            amount = BigDecimal(amount).divide(BigDecimal("100"), 2, RoundingMode.DOWN),
            platformBuyerId = buyerId,
            timePaid = parseDateTime(timePaid, "yyyyMMddHHmmss"),
            originalPayload = callbackPayload
        )
    }


    private fun createRefundParameters(trade: RefundTrade): MutableMap<String, String> {

        val parameters = mutableMapOf(
            "appid" to options.appId,
            "mch_id" to options.appAccount,
            "nonce_str" to ShortId.newId(),
            "out_refund_no" to trade.refundId,
            "total_fee" to trade.amount.multiply(BigDecimal(100)).toInt().toString(),
            "refund_fee" to trade.refundAmount.multiply(BigDecimal(100)).toInt().toString(),
            "notify_url" to this.getRefundCallbackUrl(trade.state)
        )

        if (!trade.remark.isNullOrBlank()) {
            parameters["refund_desc"] = trade.remark!!
        }

        val idKey = if (trade.isPaymentTradeId) "transaction_id" else "out_trade_no"
        parameters[idKey] = trade.paymentTradeId

        if (trade.mode == TradeMode.ISV) {
            parameters["sub_appid"] = options.subAppId
            parameters["sub_mch_id"] = trade.platformMerchantKey
        }

        trade.platformParameters.forEach { (key, value) ->
            parameters[key] = value
        }
        return parameters
    }


    protected fun refundCore(url:String, trade: RefundTrade, isRetry: Boolean = false): RefundResult {
        checkIsvParameter(trade)

        val parameters = createRefundParameters(trade)
        signatureData(parameters)
        try {
            val r = requestApi(
                options.exchange.refundUrl,
                parameters,
                WechatRefundResponse::class,
                useClientCertificate = true
            )
            return RefundResult(
                paymentProvider = this.options.providerName,
                refundId = r.outRefundNo,
                platformRefundId = r.refundId,

                paymentTradeId = r.outTradeNo,
                platformPaymentTradeId = r.transactionId,

                orderAmount = BigDecimal(r.totalFee).divide(BigDecimal(100)),
                refundAmount = BigDecimal(r.refundFee).divide(BigDecimal(100)),
                refundTime = 0,
                status = RefundStatus.Doing
            )
        } catch (e: PaymentExchangeException) {
            if (isRetry) {
                throw e
            }
            return when (e.platformErrorCode) {
                WechatPaymentErrors.R_BIZERR_NEED_RETRY -> refundCore(url, trade, isRetry = true)
                else -> throw e
            }
        }
    }

    protected fun createRefundQueryParam(query: RefundQuery): MutableMap<String, String> {
        val parameters = mutableMapOf(
            "appid" to options.appId,
            "mch_id" to options.appAccount,
            "nonce_str" to ShortId.newId(),
            "out_refund_no" to query.refundId
        )

        if (query.mode == TradeMode.ISV) {
            parameters["sub_appid"] = options.subAppId
            parameters["sub_mch_id"] = query.platformMerchantKey
        }

        return parameters
    }

    protected fun mapRefundStatus(status: String): RefundStatus {
        return when (status) {
            "SUCCESS" -> RefundStatus.Succeed
            "PROCESSING"->RefundStatus.Doing
            "REFUNDCLOSE", "CHANGE" -> RefundStatus.Fail
            else -> RefundStatus.Fail
        }
    }

    protected fun queryRefundCore(url: String, query: RefundQuery): RefundResult? {
        checkIsvParameter(query)

        val parameters = createRefundQueryParam(query)
        signatureData(parameters)
        try {
            val r = requestApi(
                options.exchange.queryRefundUrl,
                parameters,
                WechatRefundQueryResponse::class,
                useClientCertificate = true
            )
            return RefundResult(
                paymentProvider = this.options.providerName,
                refundId = r.outRefundNo,
                platformRefundId = r.refundId,

                paymentTradeId = r.outTradeNo,
                platformPaymentTradeId = r.transactionId,

                orderAmount = BigDecimal(r.totalFee).divide(BigDecimal(100)),
                refundAmount = BigDecimal(r.settlementRefundFee ?: r.refundFee).divide(BigDecimal(100)),

                refundTime = parseDateTime(r.refundSuccessTime.orEmpty()),
                status = mapRefundStatus(r.refundStatus)
            )
        } catch (e: PaymentExchangeException) {
            return when (e.platformErrorCode) {
                WechatPaymentErrors.R_REFUNDNOTEXIST -> null
                else -> throw e
            }
        }
    }

    override fun parserRefundCallbackRequest(callbackPayload: Map<String, String>): RefundResult {
        //退款通知参考 ： https://pay.weixin.qq.com/wiki/doc/api/jsapi_sl.php?chapter=9_16
        val key = DigestUtils.md5Hex(this.options.merchantKey).toLowerCase().toByteArray(Charsets.UTF_8)
        val content = Base64.getDecoder().decode(callbackPayload["req_info"])

        val xml = AesUtils.ecbPKCS7Padding.decrypt(content, key)

        val data = WechatUtilities.xmlMapper.readValue<Map<String, String>>(xml, object : TypeReference<Map<String, String>>() {})

        val time = data["success_time"] ?: throw PaymenCallbackFieldMissException(this.options.providerName, "success_time")
        val amount = (data["settlement_total_fee"] ?: data["refund_fee"]) ?: throw PaymenCallbackFieldMissException(this.options.providerName, "settlement_total_fee/refund_fee")
        val orderAmount = data["total_fee"] ?: throw PaymenCallbackFieldMissException(this.options.providerName, "total_fee")
        val status =  data["refund_status"] ?: throw PaymenCallbackFieldMissException(this.options.providerName, "refund_status")

        return RefundResult(
            platformPaymentTradeId = data["transaction_id"] ?: throw PaymenCallbackFieldMissException(this.options.providerName, "transaction_id"),
            paymentTradeId = data["out_trade_no"] ?: throw PaymenCallbackFieldMissException(this.options.providerName, "out_trade_no"),
            platformRefundId = data["refund_id"] ?: throw PaymenCallbackFieldMissException(this.options.providerName, "refund_id"),
            refundId =  data["out_refund_no"] ?: throw PaymenCallbackFieldMissException(this.options.providerName, "out_refund_no"),
            refundTime = parseDateTime(time),
            refundAmount = BigDecimal(amount).divide(BigDecimal(100)),
            orderAmount =BigDecimal(orderAmount).divide(BigDecimal(100)),
            paymentProvider = options.providerName,
            status = mapRefundStatus(status)
        )
    }
}