package com.labijie.application.payment

import com.labijie.application.payment.configuration.PaymentProperties.Companion.DEFAULT_STATE_RULE_REGEX
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

/**
 * 标识第三方平台的一条交易
 * 通常映射到第三方平台统一下单 API 入参
 */
class PlatformTrade(

    /**
     * 业务系统自己的交易 id
     */
    @get:NotBlank
    @get:Length(max=32)
    var tradeId: String = "",

    /**
     * 下单模式，普通商户下单还是服务商模式下单
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
    override var platformMerchantKey:String = "",

    /**
     * 收款金额
     */
    @get:Range(min =0, max=100000000)
    var amount:BigDecimal = BigDecimal.ZERO,

    @get:NotBlank
    @get:Length(max=128)
    var subject:String = "",

    @get:Length(max=28)
    var platformBuyerId:String = "",


    var timeoutMinutes:Int = 60 * 24,

    /**
     * 支付方法，目前仅支持小程序
     */
    var method: PaymentMethod = PaymentMethod.MiniProgram,

    /**
     * 是否允许信用卡支付
     */
    var allowCreditCard:Boolean = true,


    /**
     * 自定义参数，回调时可以获取到
     */
    @get:Length(max=64)
    @get:Pattern(regexp = "^$DEFAULT_STATE_RULE_REGEX\$")
    var state:String? = null,

    /**
     * 第三方平台提供的其他参数。
     */
    val platformParameters : MutableMap<String, String> = mutableMapOf(),

    /**
     * 场景参数
     */
    @Valid
    var scene: Any? = null
):IPlatformIsvParameter{
    var clientIPAddress: String? = null
}