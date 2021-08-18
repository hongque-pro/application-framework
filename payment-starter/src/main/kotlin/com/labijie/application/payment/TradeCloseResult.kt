package com.labijie.application.payment

import java.math.BigDecimal

data class TradeCloseResult(
    var outTradeNo: String = "",
    var status: TradeCloseStatus = TradeCloseStatus.FAIL,
    var errorCode: String = "",
)