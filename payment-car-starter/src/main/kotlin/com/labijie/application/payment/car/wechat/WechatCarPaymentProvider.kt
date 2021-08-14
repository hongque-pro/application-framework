package com.labijie.application.payment.car.wechat

import com.labijie.application.payment.*
import com.labijie.application.payment.car.CarPaymentProviders
import com.labijie.application.payment.car.getCarScene
import com.labijie.application.payment.car.scene.CarParkingScene
import com.labijie.application.payment.car.wechat.scene.WechatParkingSceneInfo
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.providers.wechat.AbstractWechatPaymenProvider
import com.labijie.application.payment.providers.wechat.IWechatPaymentSceneSupport
import com.labijie.application.payment.providers.wechat.WechatPaymentOptions
import com.labijie.application.payment.providers.wechat.model.WechatTradeQueryResponse
import com.labijie.application.thridparty.wechat.WechatResponse
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.spring.configuration.NetworkConfig
import com.labijie.infra.utils.ShortId
import java.math.BigDecimal
import javax.validation.Valid

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 15:31
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

/**
 * 微信车主平台支付提供程序。
 *
 * 参考：
 *
 * 服务商：https://pay.weixin.qq.com/wiki/doc/api/vehicle_v2_sl.php?chapter=20_982&index=2&p=202
 *
 * 商户：https://pay.weixin.qq.com/wiki/doc/api/vehicle_v2.php?chapter=20_982&index=2
 */
class WechatCarPaymentProvider(
    paymentProperties: PaymentProperties,
    networkConfig: NetworkConfig,
    options: WechatPaymentOptions,
    restTemplates: MultiRestTemplates,
    private val carPaymentOptions: WechatCarPaymentOptions
) : AbstractWechatPaymenProvider(paymentProperties, networkConfig, options, restTemplates) {

    companion object {
        const val TRADE_SCENE_PARKING = "PARKING"
        const val WECHAT_PARKING_API_VERSION = "3.0"
    }

    override val name: String
        get() = CarPaymentProviders.Wechat


    final override val allowScene: Boolean
        get() = false

    private fun buildTradeParameters(trade: PlatformTrade): MutableMap<String, String> {

        val parameters = mutableMapOf(
            "appid" to options.appId,
            "mch_id" to options.appAccount,
            "body" to trade.subject,
            "out_trade_no" to trade.tradeId,
            "total_fee" to (trade.amount.multiply(BigDecimal("100"))).toString(),
            "spbill_create_ip" to this.hostIPAddress,
            "notify_url" to this.getPaymentCallbackUrl(trade.state),
            "trade_type" to "PAP",
            "version" to WECHAT_PARKING_API_VERSION,
            "trade_scene" to TRADE_SCENE_PARKING,
            "nonce_str" to ShortId.newId()
        )
        if (trade.mode == TradeMode.ISV) {
            //服务商模式，不关心 appid,因为 appid 只能是公众号，真实的小程序 id 配置在 subAppId
            parameters["sub_appid"] = options.subAppId
            parameters["sub_mch_id"] = trade.platformMerchantKey
        }
        parameters["openid"] = trade.platformBuyerId

        //加入场景数据
        val sceneInfo = buildSceneInfo(trade.scene as CarParkingScene)
        val sceneInfoData = mapOf("scene_info" to sceneInfo)
        parameters["scene_info"] = JacksonHelper.serializeAsString(sceneInfoData)

        trade.platformParameters.forEach { (key, value) ->
            parameters[key] = value
        }
        return parameters
    }

    private fun buildSceneInfo(scene: CarParkingScene): WechatParkingSceneInfo {
        return WechatParkingSceneInfo().apply {
            this.chargingTime = ((scene.timeExit - scene.timeEnter) / 1000).toString()
            this.deductMode = when (scene.deductMode) {
                DeductMode.WITHHOLDING -> "AUTOPAY"
                DeductMode.PROACTIVE -> "PROACTIVE"
            }
            this.plateNumber = scene.carNumber
            this.parkingName = scene.parkingName
            this.startTime = getWechatDateTime(scene.timeEnter)
            this.endTime = getWechatDateTime(scene.timeExit)
        }
    }

    override fun doCreateTrade(
        trade: PlatformTrade,
        sceneSupport: IWechatPaymentSceneSupport?
    ): PaymentTradeCreationResult {

        val scene = trade.getCarScene(this.name, trade)
        val parameters = this.buildTradeParameters(trade)

        this.signatureData(parameters)

        val url = when (trade.mode) {
            TradeMode.ISV -> carPaymentOptions.isvCreateTradeUrl
            TradeMode.Merchant -> carPaymentOptions.createTradeUrl
        }
        this.requestApi(url, parameters, WechatResponse::class)
        return PaymentTradeCreationResult(trade.tradeId, this.name, null, scene.deductMode)
    }

    override fun mapTradeState(status: String): PaymentTradeStatus {
        return when (status) {
            WechatCarTradeStatus.SUCCESS,
            WechatCarTradeStatus.REFUND -> PaymentTradeStatus.Paid
            WechatCarTradeStatus.ACCEPT -> PaymentTradeStatus.WaitPay
            WechatCarTradeStatus.PAY_FAIL -> PaymentTradeStatus.Close
            else -> PaymentTradeStatus.WaitPay
        }
    }


    override fun queryTrade(@Valid query: PaymentTradeQuery): PaymentTradeQueryResult? {
        val url = when (query.mode) {
            TradeMode.ISV -> carPaymentOptions.isvQueryTradeUrl
            TradeMode.Merchant -> carPaymentOptions.queryTradeUrl
        }

        return queryTradeCore(url, query, WechatTradeQueryResponse::class) {
            this.mapTradeQueryResponse(it, query.mode)
        }
    }

    override fun transfer(trade: TransferTrade, overrideOptions: PaymentOptions?): TransferTradeResult {
        throw NotImplementedError()
    }

    override fun queryTransfer(query: PaymentTransferQuery, overrideOptions: PaymentOptions?): TransferQueryResult? {
        throw NotImplementedError()
    }

    override fun refund(trade: RefundTrade): RefundResult {
        return refundCore(carPaymentOptions.refundUrl, trade, isRetry = false)
    }

    override fun queryRefund(query: RefundQuery): RefundResult? {
        return queryRefundCore(carPaymentOptions.queryRefundUrl, query)
    }
}