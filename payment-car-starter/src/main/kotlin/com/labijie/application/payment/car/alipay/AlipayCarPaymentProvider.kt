package com.labijie.application.payment.car.alipay

import com.labijie.application.parseDateTime
import com.labijie.application.payment.*
import com.labijie.application.payment.car.CarPaymentProviders
import com.labijie.application.payment.car.alipay.model.AlipayCarTradeQueryReponse
import com.labijie.application.payment.car.alipay.model.AlipayCarTradeResponse
import com.labijie.application.payment.car.getCarScene
import com.labijie.application.payment.car.scene.CarParkingScene
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.exception.PaymentExchangeException
import com.labijie.application.payment.providers.alipay.AbstractAlipayPaymentProvider
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.providers.alipay.IAlipayPaymentSceneSupport
import com.labijie.application.web.client.MultiRestTemplates
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import java.math.BigDecimal

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/28 12:42
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

/**
 * 支付宝车主代扣提供程序。
 *
 * 参考：https://docs.alipay.com/pre-open/api_pre/alipay.eco.mycar.parking.order.pay
 */
class AlipayCarPaymentProvider(
    paymentProperties: PaymentProperties,
    options: AlipayPaymentOptions,
    restTemplates: MultiRestTemplates
) : AbstractAlipayPaymentProvider(paymentProperties, options, restTemplates) {

    override val allowScene: Boolean
        get() = false

    override val name: String
        get() = CarPaymentProviders.Alipay

    private fun buildBizContent( trade: PlatformTrade): MutableMap<String, String> {
        val scene = trade.scene as CarParkingScene
        val parameters = mutableMapOf(
            "car_number" to scene.carNumber,
            "out_trade_no" to trade.tradeId,
            "subject" to trade.subject,
            "total_fee" to String.format("%.2f", trade.amount.toDouble()),
            "parking_id" to scene.platformParkingId!!,
            "seller_id" to scene.platformSellerId!!,
            "out_parking_id" to scene.parkingId!!.toString(),
            "car_number" to scene.carNumber
        )

        if(trade.mode == TradeMode.ISV){
            parameters["agent_id"] = this.options.appAccount
        }
        else{
            parameters["seller_id"] = this.options.appAccount
        }
        return parameters
    }

    override fun doCreateTrade(
        trade: PlatformTrade,
        sceneSupport: IAlipayPaymentSceneSupport?
    ): PaymentTradeCreationResult {

        val scene = trade.getCarScene(this.name, trade)
        if(scene.platformParkingId.isNullOrBlank()){
            throw IllegalArgumentException("To use alipay car payment, the platformParkingId can not be null.")
        }
        if (scene.platformSellerId.isNullOrBlank()){
            throw IllegalArgumentException("To use alipay car payment, the platformSellerId can not be null.")
        }
        if (scene.parkingId == null || scene.parkingId!! <= 0) {
            throw IllegalArgumentException("To use alipay car payment, the parkingId can not be null.")
        }
        val mntAuthCode = if (trade.mode == TradeMode.ISV) trade.platformMerchantKey else null
        val parameter = this.buildCommonsParams("alipay.eco.mycar.parking.order.pay", mntAuthCode)
        val bizContent = buildBizContent(trade)

        combineBizContent(parameter, bizContent)
        signatureData(parameter)

        val response = this.requestApi(this.options.exchangeUrl, parameter, AlipayCarTradeResponse::class)
        return PaymentTradeCreationResult(response.outTradeNo, this.name, response.bizTradeNo, DeductMode.WITHHOLDING, false)
    }

    override fun parsePaymentCallbackRequest(callbackPayload: Map<String, String>): PaymentCallbackRequest {
         throw UnsupportedOperationException()
    }

    override fun parseCallbackResponse(isSuccess: Boolean, errorMessage: String?): CallbackResponse {
        throw UnsupportedOperationException()
    }

    override fun transfer(trade: TransferTrade, overrideOptions: PaymentOptions?): TransferTradeResult {
        throw NotImplementedError()
    }

    override fun queryTransfer(query: PaymentTransferQuery, overrideOptions: PaymentOptions?): TransferQueryResult? {
        throw NotImplementedError()
    }

    /**
     *
     * 0：创建；1：支付成功；2：支付失败；3：支付完成；4：支付中；5：交易关闭；6：转为退款；8：退款成功；7：退款失败；9：交易完结
     */
    override fun mapAlipayStatus(alipayTradeStatuts: String): PaymentTradeStatus {
        return when(alipayTradeStatuts){
            "4"-> PaymentTradeStatus.WaitPay
            "2", "5", "8", "9"->PaymentTradeStatus.Close
            "1", "3", "6", "7"->PaymentTradeStatus.Paid
            else-> {
                logger.warn("Unknown alipay trade status : $alipayTradeStatuts, and consider as WaitPay")
                PaymentTradeStatus.WaitPay
            }
        }
    }

    //统一下单 API 无法查询，支付宝文档错误？
    override fun queryTrade(query: PaymentTradeQuery): PaymentTradeQueryResult? {
        this.checkIsvParameter(query)
        //参考 ：https://docs.alipay.com/pre-open/api_pre/alipay.eco.mycar.trade.order.query
        val mntAuthCode = if (query.mode == TradeMode.ISV) query.platformMerchantKey else null
        val parameters = this.buildCommonsParams("alipay.eco.mycar.trade.order.query", mntAuthCode)

        val idField = if(query.isPlatformTradeId) "biz_trade_no" else "out_biz_trade_no"
        val bizContent = mapOf(
            idField to query.id
        )
        this.combineBizContent(parameters, bizContent)

        this.signatureData(parameters)
        try {
            val response = this.requestApi(this.options.exchangeUrl, parameters, AlipayCarTradeQueryReponse::class)
            val status = mapAlipayStatus(response.tradeStatus)
            val tradeId = if(!query.isPlatformTradeId) query.id else ""
            return PaymentTradeQueryResult(
                response.outBizTradeNo.ifNullOrBlank { tradeId },
                response.bizTradeNo,
                platformBuyerId=response.buyerId,
                amount = BigDecimal(response.totalFee),
                status = status,
                platformStatus = response.tradeStatus,
                paidTime = parseDateTime(response.gmtPaymentSuccess, "yyyy-MM-dd HH:mm:ss")
            )
        }
        catch (e:PaymentExchangeException){
            if(e.platformErrorCode == AlipayCarPaymentErrors.OrderNotExist){
                return null
            }
            throw e
        }
    }
}