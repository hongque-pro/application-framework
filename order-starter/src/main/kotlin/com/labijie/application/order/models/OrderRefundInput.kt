package com.labijie.application.order.models

import com.labijie.application.payment.TradeMode

data class OrderRefundInput(
    var orderId: Long,
    var platformMerchantKey: String?,
    var remark: String? = null,
    var mode: TradeMode = TradeMode.ISV

)