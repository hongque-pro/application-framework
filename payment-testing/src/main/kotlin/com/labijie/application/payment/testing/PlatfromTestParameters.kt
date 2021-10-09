package com.labijie.application.payment.testing

import com.labijie.application.payment.PaymentMethod
import com.labijie.application.payment.TradeMode
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import java.math.BigDecimal
import javax.validation.constraints.NotBlank

class PlatfromTestParameters {
    var platformMerchantKey:String = ""
    var subject:String = ""
    var platformBuyerId:String = ""
    var timeoutMinutes:Int = 60 * 24

    /**
     * 用于测试付款的收款人真实姓名
     */
    var buyerRealName:String? = null

}