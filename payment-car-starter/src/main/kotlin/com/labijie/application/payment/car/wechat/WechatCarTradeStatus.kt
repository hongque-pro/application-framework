package com.labijie.application.payment.car.wechat

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 21:43
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
object WechatCarTradeStatus {
    /**
     * 支付成功
     */
    const val SUCCESS = "SUCCESS"
    /**
     * 已接收，等待扣款
     */
    const val ACCEPT = "ACCEPT"
    /**
     * 支付失败(其他原因，如银行返回失败)
     */
    const val PAY_FAIL="PAY_FAIL"
    /**
     * 转入退款
     */
    const val REFUND="REFUND"
}