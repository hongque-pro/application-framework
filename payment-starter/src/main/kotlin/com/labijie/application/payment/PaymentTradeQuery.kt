package com.labijie.application.payment

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class PaymentTradeQuery(
    /**
     * 查询的交易 id, 根据 @see isPlatformTradeId 确定是业务系统的交易 id 还是第三方平台的交易 id。
     *
     * 应该优先使用第三方平台的交易 id.
     */
    @get:NotBlank
    val id:String = "",

    val isPlatformTradeId:Boolean = true,
    /**
     * 交易属于
     */
    override var mode: TradeMode = TradeMode.ISV,

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
    override var platformMerchantKey:String = ""

) : IPlatformIsvParameter