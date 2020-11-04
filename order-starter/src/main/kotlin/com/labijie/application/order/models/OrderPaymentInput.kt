package com.labijie.application.order.models

import com.labijie.application.payment.DeductMode
import com.labijie.application.payment.PaymentMethod
import com.labijie.application.payment.TradeMode
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class OrderPaymentInput(

    @get:Min(1)
    var orderId:Long,
    /**
     * 签约商户的收款账号（仅 ISV 模式有效）
     *
     * 支付宝：
     *
     * 应用授权后获得的 app_auth_token
     *
     * 微信：
     *
     * 服务商创建的 sub_mch_id
     *
     */
    var platformMerchantKey:String,

    @get:Min(1)
    var userId:Long,

    @get:Length(max=28)
    var platformBuyerId:String,

    @get:NotBlank
    @get:Length(max= 16)
    var paymentProvider:String,

    var timeoutMinutes:Int = 60 * 24,

    /**
     * 下单模式，普通商户下单还是服务商模式下单
     */
    var mode: TradeMode = TradeMode.ISV,


    /**
     * 扣款方式。
     */
    var deductMode:DeductMode = DeductMode.PROACTIVE,

    /**
     * 支付方法，目前仅支持小程序
     */
    var method: PaymentMethod = PaymentMethod.MiniProgram,

    /**
     * 用于需要在上游生成tradeId的情况
     */
    var paymentTradeId: Long? = null
)