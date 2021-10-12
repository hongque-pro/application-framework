package com.labijie.application.payment

import com.labijie.application.payment.exception.UnsupportedPaymentProviderException

class PaymentUtils(
    private val paymentProviders: Map<String, IPaymentProvider>)  {

    val supportedProviders by lazy {  paymentProviders.map { it.key }.toTypedArray() }

    private fun validateProviderName(providerName:String){
        if(providerName.isBlank()){
            throw IllegalArgumentException("Payment provider name cant not be null or empty string (parameter name : providerName) .")
        }
        if(!supportedProviders.contains(providerName)){
            throw UnsupportedPaymentProviderException(providerName)
        }
    }

    fun getPaymentProvider(providerName:String) : IPaymentProvider{
        this.validateProviderName(providerName)
       return this.paymentProviders[providerName] ?: throw UnsupportedPaymentProviderException(providerName)
    }

    fun createTrade(providerName:String, trade: PlatformTrade): PaymentTradeCreationResult{
        this.validateProviderName(providerName)

        val provider =getPaymentProvider(providerName)
        return provider.createTrade(trade)
    }

    fun closeTrade(providerName: String, close: TradeCloseParam): TradeCloseResult {
        this.validateProviderName(providerName)

        val provider =getPaymentProvider(providerName)
        return provider.closeTrade(close)
    }

    fun queryTrade(providerName:String, query: PaymentTradeQuery): PaymentTradeQueryResult? {
        this.validateProviderName(providerName)

        val provider =getPaymentProvider(providerName)
        return provider.queryTrade(query)
    }

    /**
     * 转账接口
     */
    fun transfer(providerName:String, trade: TransferTrade, overrideOptions: PaymentOptions? = null): TransferTradeResult{
        this.validateProviderName(providerName)

        val provider =getPaymentProvider(providerName)
        return provider.transfer(trade, overrideOptions)
    }

    /**
     * 查询转账单（必须同时提供业务单号和平台单号）
     */
    fun queryTransfer(providerName:String, query: TransferQuery, overrideOptions: PaymentOptions? = null): TransferQueryResult?{
        this.validateProviderName(providerName)

        val provider =getPaymentProvider(providerName)
        return provider.queryTransfer(query, overrideOptions)
    }

    fun refund(providerName:String,trade:RefundTrade): RefundResult{
        this.validateProviderName(providerName)

        val provider =getPaymentProvider(providerName)
        return provider.refund(trade)
    }

    fun queryRefund(providerName:String,query:RefundQuery): RefundResult? {
        this.validateProviderName(providerName)

        val provider =getPaymentProvider(providerName)
        return provider.queryRefund(query)
    }
}