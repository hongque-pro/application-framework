package com.labijie.application.payment

data class TransferTradeResult(
    var platformTradeId:String,
    var tradeId:String,
    var timePaid:Long,
    var status: TransferStatus
)