package com.labijie.application.payment

abstract class PaymentOptions {
    var appId: String = ""
    var appSecret: String = ""
    var appAccount:String = ""
    abstract val providerName: String
}