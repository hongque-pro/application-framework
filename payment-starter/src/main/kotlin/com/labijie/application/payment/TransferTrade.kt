package com.labijie.application.payment

import org.hibernate.validator.constraints.Length
import java.math.BigDecimal

data class TransferTrade(
    /**
     * 业务系统的交易 id
     */
    var tradeId:String,
    /**
     * 平台方提供的收款方 id
     * 对于微信是 openId, 对于支付宝是 uid
     */
    var platformPayeeId: String = "",
    /**
     * 转账金额（元）
     */
    var amount:BigDecimal = BigDecimal.ZERO,
    /**
     * 收款人真实姓名，如果提供收款人真实姓名，平台将对姓名进行验证
     */
    var payeeRealName:String? = null,

    var title:String? = null,

    /**
     * 备注（100个字符以内）
     */
    @get:Length(max=100)
    var remark: String? = null,

    override var mode: TradeMode = TradeMode.Merchant,

    override var platformMerchantKey: String = ""
) : IPlatformIsvParameter