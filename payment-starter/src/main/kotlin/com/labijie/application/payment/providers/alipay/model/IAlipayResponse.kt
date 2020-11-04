package com.labijie.application.payment.providers.alipay.model

import com.labijie.application.thridparty.alipay.AlipayResponseBase

interface IAlipayResponse<TModel: AlipayResponseBase> {
    var response: TModel
    var sign:String?
}