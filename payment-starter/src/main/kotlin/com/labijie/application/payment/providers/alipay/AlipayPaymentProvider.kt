package com.labijie.application.payment.providers.alipay

import com.labijie.application.parseDateTime
import com.labijie.application.payment.*
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.exception.PaymenCallbackFieldMissException
import com.labijie.application.payment.exception.PaymentSignatureException
import com.labijie.application.payment.providers.alipay.model.AlipayPayeeInfo
import com.labijie.application.payment.providers.alipay.model.AlipayTradeCreationReponse
import com.labijie.application.payment.providers.alipay.model.AlipayTransferQueryResponse
import com.labijie.application.payment.providers.alipay.model.AlipayTransferResponse
import com.labijie.application.payment.scene.TradeParameterEffect
import com.labijie.application.payment.scene.TradeParameterEffectGeneric
import com.labijie.application.thridparty.alipay.AlipayUtilities
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.utils.ifNullOrBlank
import java.math.BigDecimal

class AlipayPaymentProvider(
    paymentProperties: PaymentProperties,
    options: AlipayPaymentOptions,
    restTemplates: MultiRestTemplates
) :
    AbstractAlipayPaymentProvider(paymentProperties, options, restTemplates) {


    override val protocol: ExchangeProtocol
        get() = ExchangeProtocol.Form

    override fun parseCallbackResponse(isSuccess: Boolean, errorMessage: String?): CallbackResponse {
        return if (isSuccess) CallbackResponse("success") else CallbackResponse(errorMessage.ifNullOrBlank { "fault" })
    }

    override fun parsePaymentCallbackRequest(callbackPayload: Map<String, String>): PaymentCallbackRequest {
        //解析回调，参考：https://docs.open.alipay.com/194/103296/
        val tables = mutableMapOf(*callbackPayload.toList().toTypedArray())
        val sign = tables.remove("sign") ?: throw PaymenCallbackFieldMissException(options.providerName, "sign")
        val signType =
            tables.remove("sign_type") ?: throw PaymenCallbackFieldMissException(options.providerName, "sign_type")
        //验签
        if (!AlipayUtilities.verifySignature(sign, options.alipayPubKey, tables, signType)) {
            throw PaymentSignatureException(options.providerName)
        }

        val appid = tables["app_id"].ifNullOrBlank { options.appId }
        val tradeId =
            tables["out_trade_no"] ?: throw PaymenCallbackFieldMissException(options.providerName, "out_trade_no")
        val platformTradeId =
            tables["trade_no"] ?: throw PaymenCallbackFieldMissException(options.providerName, "trade_no")
        val status =
            tables["trade_status"] ?: throw PaymenCallbackFieldMissException(options.providerName, "trade_status")
        val timePaid =
            tables["gmt_payment"] ?: throw PaymenCallbackFieldMissException(options.providerName, "gmt_payment")
        val buyerId = tables["buyer_id"]
        val amount =
            tables["total_amount"] ?: throw PaymenCallbackFieldMissException(options.providerName, "gmt_payment")

        return PaymentCallbackRequest(
            appid,
            tradeId,
            platformTradeId,
            mapAlipayStatus(status),
            amount = BigDecimal(amount),
            platformBuyerId = buyerId,
            timePaid = parseDateTime(timePaid, "yyyy-MM-dd HH:mm:ss"),
            originalPayload = callbackPayload
        )
    }


    private fun buildCreationBizContent(
        context: AlipayPaymentContext?,
        sceneSupport: IAlipayPaymentSceneSupport?,
        trade: PlatformTrade
    ): MutableMap<String, Any> {
        val timeoutMinutes = Math.min(trade.timeoutMinutes, 60 * 15)

        val map = mutableMapOf<String, Any>(
            "out_trade_no" to trade.tradeId,
            "total_amount" to String.format("%.2f", trade.amount.toDouble()),
            "subject" to trade.subject,
            "timeout_express" to "${timeoutMinutes}m",
            "buyer_id" to trade.platformBuyerId
        )
        val extend = mutableMapOf<String, Any>()
        if (trade.mode != TradeMode.ISV) {
            //收款账号，服务商模式默认到 app_token 对应账号，因此不用填写，非服务商模式可以指定
            map["seller_id"] = options.appAccount
        } else {
            //服务商模式，附加返利的服务商收款账号
            extend["sys_service_provider_id"] = options.appAccount
            //TODO：或许服务商模式也应该能够指定商户的收款账号？
            //map["seller_id"] = XXX
        }

        this.applySceneForTradeParametersExtendParams(context, sceneSupport, trade, extend)
        if (extend.isNotEmpty()) {
            map["extend_params"] = extend
        }
        trade.platformParameters.forEach { (key, value) ->
            map[key] = value
        }
        return map
    }

    private fun applySceneForTradeParameters(
        context: AlipayPaymentContext?,
        sceneSupport: IAlipayPaymentSceneSupport?,
        trade: PlatformTrade,
        parameters: MutableMap<String, String>
    ) {
        if (context != null && sceneSupport != null) {
            sceneSupport.effectTradeParameters(context, TradeParameterEffect(trade, parameters))
        }
    }

    private fun applySceneForTradeParametersExtendParams(
        context: AlipayPaymentContext?,
        sceneSupport: IAlipayPaymentSceneSupport?,
        trade: PlatformTrade,
        parameters: MutableMap<String, Any>
    ) {
        if (context != null && sceneSupport != null) {
            sceneSupport.effectTradeParametersExtendParams(context, TradeParameterEffectGeneric(trade, parameters))
        }
    }

    private fun applySceneForTradeParametersBizContent(
        context: AlipayPaymentContext?,
        sceneSupport: IAlipayPaymentSceneSupport?,
        trade: PlatformTrade,
        parameters: MutableMap<String, Any>
    ) {
        if (context != null && sceneSupport != null) {
            sceneSupport.effectTradeParametersBizContent(context, TradeParameterEffectGeneric(trade, parameters))
        }
    }

    override fun doCreateTrade(
        trade: PlatformTrade,
        sceneSupport: IAlipayPaymentSceneSupport?
    ): PaymentTradeCreationResult {
        //参考文档：https://docs.open.alipay.com/api_1/alipay.trade.create/

        //节约内存，没有场景支持时候不需要创建上下文
        val context = if (sceneSupport == null) null else
            AlipayPaymentContext(trade.mode, this, paymentProperties, options)

        val mntAuthCode = if (trade.mode == TradeMode.ISV) trade.platformMerchantKey else null
        val parameters = buildCommonsParams("alipay.trade.create", mntAuthCode, this.getPaymentCallbackUrl(trade.state))
        val bizContent = buildCreationBizContent(context, sceneSupport, trade)

        applySceneForTradeParametersBizContent(context, sceneSupport, trade, bizContent)
        combineBizContent(parameters, bizContent)

        this.applySceneForTradeParameters(context, sceneSupport, trade, parameters)

        signatureData(parameters)
        val data = requestApi(
            options.exchangeUrl,
            parameters,
            AlipayTradeCreationReponse::class
        )

        return PaymentTradeCreationResult(trade.tradeId, options.providerName, data.tradeNo)
    }

    private fun buildTransferBizContent(trade: TransferTrade): MutableMap<String, Any> {

        val map = mutableMapOf<String, Any>(
            "out_biz_no" to trade.tradeId,
            "trans_amount" to String.format("%.2f", trade.amount.toDouble()),
            "product_code" to "TRANS_ACCOUNT_NO_PWD",
            "biz_scene" to "DIRECT_TRANSFER"
        )
        if (!trade.remark.isNullOrBlank()) {
            map["remark"] = trade.remark.orEmpty()
        }
        if (!trade.title.isNullOrBlank()) {
            map["order_title"] = trade.title.orEmpty()
        }

        map["payee_info"] = AlipayPayeeInfo(trade.platformPayeeId, name = trade.payeeRealName)

        return map
    }

    private fun mapTransferStatus(statusValue: String): TransferStatus {
        return when (statusValue) {
            "SUCCESS" -> TransferStatus.Succeed
            "REFUND" -> TransferStatus.Refund
            "FAIL" -> TransferStatus.Fail
            else->TransferStatus.Doing
        }
    }

    override fun transfer(trade: TransferTrade, overrideOptions: PaymentOptions?): TransferTradeResult {
        val usedOptions = checkPaymentOptions(overrideOptions) ?: this.options

        //参考： https://opendocs.alipay.com/apis/api_28/alipay.fund.trans.uni.transfer
        val mntAuthCode = if (trade.mode == TradeMode.ISV) trade.platformMerchantKey else null
        val parameters =
            buildCommonsParams("alipay.fund.trans.uni.transfer", mntAuthCode, overrideOptions = usedOptions)
        val bizContent = buildTransferBizContent(trade)
        combineBizContent(parameters, bizContent)

        signatureData(parameters, usedOptions)

        val data = requestApi(
            options.exchangeUrl,
            parameters,
            AlipayTransferResponse::class,
            usedOptions
        )

        return TransferTradeResult(
            platformTradeId = data.orderId,
            tradeId = data.outBizNo,
            timePaid = parseDateTime(data.transDate),
            status = mapTransferStatus(data.status)
        )
    }


    override fun queryTransfer(query: PaymentTransferQuery, overrideOptions: PaymentOptions?): TransferQueryResult? {
        //参考：https://opendocs.alipay.com/apis/api_28/alipay.fund.trans.order.query
        val usedOptions = checkPaymentOptions(overrideOptions) ?: this.options
        val mntAuthCode = if (query.mode == TradeMode.ISV) query.platformMerchantKey else null
        val parameters =
            buildCommonsParams("alipay.fund.trans.order.query", mntAuthCode, overrideOptions = usedOptions)
        val bizContent = mapOf<String, Any>(
            "order_id" to query.platformTradeId
        )
        combineBizContent(parameters, bizContent)

        signatureData(parameters, usedOptions)

        val data = requestApi(
            options.exchangeUrl,
            parameters,
            AlipayTransferQueryResponse::class,
            usedOptions
        )

        val status = mapTransferStatus(data.status)
        return TransferQueryResult(
            data.outBizNo,
            data.orderId,
            parseDateTime(data.payDate)).apply {
            if(status == TransferStatus.Fail || !data.errorCode.isNullOrBlank()){
                errorCode = data.errorCode
                errorDescription = data.failReason
            }
        }
    }

}