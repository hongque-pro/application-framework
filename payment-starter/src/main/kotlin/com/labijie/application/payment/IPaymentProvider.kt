package com.labijie.application.payment

import javax.validation.Valid

interface IPaymentProvider {
    val name: String

    val protocol: ExchangeProtocol
        get() = ExchangeProtocol.Json

    /**
     *  向支付平台发起下单
     */
    fun createTrade(@Valid trade: PlatformTrade): PaymentTradeCreationResult
//    fun signature(params: Map<String, String>, signType: String = ""): String
//    fun verifySignature(sign: String, params: Map<String, String>, signType: String = ""): Boolean


    fun parserRefundCallbackRequest(callbackPayload: Map<String, String>): RefundResult
    /**
     * 对回调的数据进行统一解析，返回统一格式回调请求
     */
    fun parsePaymentCallbackRequest(callbackPayload: Map<String, String>): PaymentCallbackRequest

    /**
     * 为支付平台返回响应
     */
    fun parseCallbackResponse(isSuccess: Boolean, errorMessage: String? = null): CallbackResponse

    /**
     * 从支付平台查询支付交易
     */
    fun queryTrade(@Valid query: PaymentTradeQuery): PaymentTradeQueryResult?

    fun transfer(@Valid trade: TransferTrade, overrideOptions: PaymentOptions? = null): TransferTradeResult

    /**
     * 查询转账单（必须同时提供业务单号和平台单号）
     */
    fun queryTransfer(query: PaymentTransferQuery, overrideOptions: PaymentOptions? = null): TransferQueryResult?

    /**
     * 申请退款
     */
    fun refund(@Valid trade:RefundTrade): RefundResult

    /**
     * 查询退款
     */
    fun queryRefund(@Valid query: RefundQuery): RefundResult?
}