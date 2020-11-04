package com.labijie.application.payment

import javax.validation.constraints.NotBlank

interface IPlatformIsvParameter {
    var mode: TradeMode
    var platformMerchantKey:String
}