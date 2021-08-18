package com.labijie.application.payment.providers.wechat

import com.labijie.application.exception.ThirdPartyExchangeException
import com.labijie.application.parseDateTime
import com.labijie.application.payment.*
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.exception.PaymentException
import com.labijie.application.payment.exception.PaymentExchangeException
import com.labijie.application.payment.exception.TransferException
import com.labijie.application.payment.providers.wechat.model.*
import com.labijie.application.payment.scene.TradeParameterEffect
import com.labijie.application.thridparty.wechat.WechatUtilities
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.spring.configuration.NetworkConfig
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.validation.Valid


class WechatPaymentProvider(
    paymentProperties: PaymentProperties,
    networkConfig: NetworkConfig,
    options: WechatPaymentOptions,
    restTemplates: MultiRestTemplates
) : AbstractWechatPaymenProvider(paymentProperties, networkConfig, options, restTemplates) {

    override fun queryTrade(@Valid query: PaymentTradeQuery): PaymentTradeQueryResult? {
        return queryTradeCore(options.exchange.queryTradeUrl, query, WechatTradeQueryResponse::class) {
            this.mapTradeQueryResponse(it, query.mode)
        }
    }

    final override val protocol: ExchangeProtocol
        get() = ExchangeProtocol.Xml

    private fun createTradeParameters(trade: PlatformTrade): MutableMap<String, String> {
        val startTime = LocalDateTime.now(ZoneOffset.ofHours(8))
        val endTime = startTime.plusMinutes(trade.timeoutMinutes.toLong())
        val parameters = mutableMapOf(
            "appid" to options.appId,
            "mch_id" to options.appAccount,
            "body" to trade.subject,
            "out_trade_no" to trade.tradeId,
            "total_fee" to (trade.amount.toDouble() * 100).toInt().toString(),
            "spbill_create_ip" to trade.clientIPAddress.ifNullOrBlank { this.hostIPAddress },
            "time_start" to startTime.format(WechatUtilities.DATETIME_FORMAT),
            "time_expire" to endTime.format(WechatUtilities.DATETIME_FORMAT),
            "notify_url" to this.getPaymentCallbackUrl(trade.state),
            "trade_type" to getTradeType(trade.method),
            "nonce_str" to ShortId.newId()
        )
        if (!trade.allowCreditCard) {
            parameters["limit_pay"] = "no_credit"
        }
        if (trade.mode == TradeMode.ISV) {
            //服务商模式，不关心 appid,因为 appid 只能是公众号，真实的小程序 id 配置在 subAppId
            parameters["sub_appid"] = options.subAppId
            parameters["sub_openid"] = trade.platformBuyerId
            parameters["sub_mch_id"] = trade.platformMerchantKey
        } else {
            if(!trade.platformBuyerId.isBlank()) {
                parameters["openid"] = trade.platformBuyerId
            }
        }
        trade.platformParameters.forEach { (key, value) ->
            parameters[key] = value
        }
        return parameters
    }

    private fun getTradeType(method: PaymentMethod): String {
        //"JSAPI", "NATIVE", "APP", "MWEB"
        return when (method) {
            PaymentMethod.MiniProgram -> "JSAPI"
            PaymentMethod.App -> "APP"
        }
    }

    private fun applySceneForTradeParameters(
        context: WechatPaymentContext?,
        sceneSupport: IWechatPaymentSceneSupport?,
        trade: PlatformTrade,
        parameters: MutableMap<String, String>
    ) {
        if (context != null && sceneSupport != null) {
            sceneSupport.effectTradeParameters(context, TradeParameterEffect(trade, parameters))
        }
    }

    override fun doCreateTrade(
        trade: PlatformTrade,
        sceneSupport: IWechatPaymentSceneSupport?
    ): PaymentTradeCreationResult {
        //普通商户参考：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1
        //ISV 参考：https://pay.weixin.qq.com/wiki/doc/api/jsapi_sl.php?chapter=9_1
        val parameter = createTradeParameters(trade)


        //节约内存，没有场景支持时候不需要创建上下文
        val context =
            if (sceneSupport == null) null else WechatPaymentContext(trade.mode, this, paymentProperties, options)

        this.applySceneForTradeParameters(context, sceneSupport, trade, parameter)

        signatureData(parameter)

        val r = requestApi(options.exchange.createTradeUrl, parameter, WechatTradeCreationResponse::class)

        val data = createTradeTag(r.prepayId, trade)

        return PaymentTradeCreationResult(trade.tradeId, options.providerName, platformTradeId = null, tag = data)
    }


    /**
     * 微信反人类 api， 要根据不同支付方式附加不同的返回数据，为微信支付创建附加数据
     */
    private fun createTradeTag(
        platformTradeId: String,
        trade: PlatformTrade
    ): Map<String, String> {
        return when (trade.method) {
            //小程序参考：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=3
            PaymentMethod.MiniProgram -> {
                val data = mutableMapOf(
                    "appId" to (if (trade.mode == TradeMode.ISV) options.subAppId else options.appId),
                    "timeStamp" to Instant.now().epochSecond.toString(),
                    "nonceStr" to ShortId.newId(),
                    "package" to "prepay_id=$platformTradeId"
                )
                signatureData(data, "paySign", "signType")
                data.remove("appId")
                data
            }
            PaymentMethod.App -> {
                //APP 参考：https://pay.weixin.qq.com/wiki/doc/api/app/app_sl.php?chapter=9_12&index=2
                val data = mutableMapOf(
                    "appid" to (if (trade.mode == TradeMode.ISV) options.subAppId else options.appId),
                    "partnerid" to (if (trade.mode == TradeMode.ISV) trade.platformMerchantKey else options.appAccount),
                    "noncestr" to ShortId.newId(),
                    "prepayid" to platformTradeId,
                    "package" to "Sign=WXPay",
                    "timestamp" to Instant.now().epochSecond.toString()
                )
                signatureData(data, joinSignType = false)

                return data
            }
        }
    }

    override fun queryTransfer(query: PaymentTransferQuery, overrideOptions: PaymentOptions?): TransferQueryResult? {
        if (query.mode == TradeMode.ISV) {
            throw TransferException("Isv mode for wechat transfer was not supported")
        }
        val useOptions = this.checkPaymentOptions(overrideOptions) ?: this.options
        val parameters = mutableMapOf(
            "appid" to useOptions.appId,
            "mch_id" to useOptions.appAccount,
            "partner_trade_no" to query.tradeId
        )
        signatureData(parameters, overrideOptions = useOptions)
        val response =
            try {
                requestApi(
                    useOptions.exchange.queryTransferUrl,
                    parameters,
                    WechatTransferQueryResponse::class,
                    useClientCertificate = false,
                    overrideOptions = useOptions
                )
            } catch (e: PaymentExchangeException) {
                if (e.platformErrorCode == WechatPaymentErrors.TRANSFER_NOT_FOUND) {
                    return null
                }
                throw e
            }
        val status = when (response.status) {
            "SUCCESS" -> TransferStatus.Succeed
            "FAILED" -> TransferStatus.Fail
            "PROCESSING" -> TransferStatus.Doing
            else -> TransferStatus.Fail
        }

        return TransferQueryResult(
            response.partnerTradeNo,
            response.detailId,
            parseDateTime(response.transferTime)
        ).apply {
            if (status == TransferStatus.Fail) {
                errorCode = "ERROR"
                errorDescription = response.reason
            }
        }
    }

    override fun transfer(trade: TransferTrade, overrideOptions: PaymentOptions?): TransferTradeResult {
        //参考： https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
        if (trade.mode == TradeMode.ISV) {
            throw TransferException("Isv mode for wechat transfer was not supported")
        }
        val useOptions = this.checkPaymentOptions(overrideOptions) ?: this.options


        val parameters = mutableMapOf(
            "mch_appid" to useOptions.appId,
            "mchid" to useOptions.appAccount,
            "nonceStr" to ShortId.newId(),
            "partner_trade_no" to trade.tradeId,
            "openid" to trade.platformPayeeId,
            "check_name" to if (trade.payeeRealName.isNullOrBlank()) "NO_CHECK" else "FORCE_CHECK",
            "amount" to (trade.amount.toDouble() * 100).toInt().toString(),
            "desc" to trade.remark.ifNullOrBlank { "OTHERS" },
            "spbill_create_ip" to this.hostIPAddress
        )
        if (!trade.payeeRealName.isNullOrBlank()) {
            parameters["re_user_name"] = trade.payeeRealName.orEmpty()
        }
        signatureData(parameters, overrideOptions = useOptions)
        val response = requestTransfer(trade, parameters, useOptions)
        return TransferTradeResult(
            response.partnerTradeNo,
            response.paymentNo,
            parseDateTime(response.paymentTime),
            TransferStatus.Succeed
        )
    }

    private fun requestTransfer(
        trade: TransferTrade,
        parameters: MutableMap<String, String>,
        useOptions: WechatPaymentOptions
    ): WechatTransferResponse {
        return try {
            requestApi(
                useOptions.exchange.transferUrl,
                parameters,
                WechatTransferResponse::class,
                useClientCertificate = true,
                overrideOptions = useOptions
            )
        } catch (ex: PaymentExchangeException) {
            if (ex.platformErrorCode != WechatPaymentErrors.SYSTEMERROR) {
                throw ex
            }
            //出错时候并且是 SYSTEMERROR 时查询一次
            val query = PaymentTransferQuery(trade.tradeId, "", trade.mode, trade.platformMerchantKey)
            val r = queryTransfer(query, useOptions) ?: throw ex
            WechatTransferResponse().apply {
                val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val timeString =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(r.timePaid), ZoneOffset.ofHours(8)).format(pattern)

                this.partnerTradeNo = r.tradeId
                this.paymentNo = r.platformTradeId
                this.paymentTime = timeString
            }
        }
    }


    override fun refund(trade: RefundTrade): RefundResult {
        return refundCore(options.exchange.refundUrl, trade, isRetry = false)
    }

    override fun queryRefund(query: RefundQuery): RefundResult? {
        return queryRefundCore(options.exchange.queryRefundUrl, query)
    }

    override fun closeTrade(param: TradeCloseParam): TradeCloseResult {
        if(param.outTradeNo.isNullOrBlank()) {
            throw PaymentException(options.providerName, "未传入outTradeNo")
        }

        val parameters = mutableMapOf(
                "appid" to options.appId,
                "mch_id" to options.appAccount,
                "out_trade_no" to (param.outTradeNo ?: ""),
                "nonce_str" to ShortId.newId(),
        )
        signatureData(parameters)

        try {
            requestApi(options.exchange.closeTradeUrl, parameters, WechatCloseResponse::class)
            return TradeCloseResult(param.outTradeNo!!, TradeCloseStatus.SUCCESS)
        } catch (e: PaymentExchangeException) {
            if(e.platformErrorCode == "ORDERPAID") {
                return TradeCloseResult(param.outTradeNo!!, TradeCloseStatus.ORDER_PAID)
            } else if(e.platformErrorCode == "ORDERCLOSED") {
                return TradeCloseResult(param.outTradeNo!!, TradeCloseStatus.ORDER_CLOSED)
            } else {
                logger.error("关闭订单出错", e)
                return TradeCloseResult(param.outTradeNo!!, TradeCloseStatus.FAIL, e.platformErrorCode ?: "UNKNOW")
            }
        }

    }
}