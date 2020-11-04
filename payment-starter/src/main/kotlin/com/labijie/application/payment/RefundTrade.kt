package com.labijie.application.payment

import com.labijie.application.payment.configuration.PaymentProperties
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.validation.constraints.Pattern

data class RefundTrade(
    /**
     * 退款单号
     */
    var refundId: String,
    /**
     * 付款交易单号
     */
    var paymentTradeId: String,
    var amount: BigDecimal,

    @get:Length(max = 80)
    var remark: String? = null,
    /**
     * 是否使用平台的付款单号
     */
    var isPaymentTradeId: Boolean = false,
    override var platformMerchantKey: String = "",
    override var mode: TradeMode = TradeMode.Merchant
) : IPlatformIsvParameter {
    var refundAmount: BigDecimal = amount

    val platformParameters: MutableMap<String, String> = mutableMapOf()

    @get:Length(max=64)
    @get:Pattern(regexp = "^${PaymentProperties.DEFAULT_STATE_RULE_REGEX}\$")
    var state:String? = null
}