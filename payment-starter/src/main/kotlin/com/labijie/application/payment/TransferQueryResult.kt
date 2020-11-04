package com.labijie.application.payment

import com.labijie.application.payment.TransferTrade

data class TransferQueryResult(
    /**
     * 业务系统的交易 id
     */
    var tradeId:String,
    /**
     * 第三方平台单号
     */
    var platformTradeId: String = "",

    var timePaid: Long,

    var errorCode:String? = null,
    var errorDescription:String? = null
)