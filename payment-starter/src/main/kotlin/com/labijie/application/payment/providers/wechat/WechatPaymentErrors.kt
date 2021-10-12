package com.labijie.application.payment.providers.wechat

object WechatPaymentErrors {
    const val SYSTEMERROR = "SYSTEMERROR"
    const val QUERY_ORDERNOTEXIST = "ORDERNOTEXIST"
    const val OUT_TRADE_NO_USED = "OUT_TRADE_NO_USED"


    //退款： https://pay.weixin.qq.com/wiki/doc/api/vehicle_v2_sl.php?chapter=9_4&index=5&p=202
    const val R_BIZERR_NEED_RETRY = "BIZERR_NEED_RETRY"
    const val R_REFUNDNOTEXIST = "REFUNDNOTEXIST"


    const val TRANSFER_NOT_FOUND = "NOT_FOUND"
    const val TRANSFER_NOT_ENOUGH="NOTENOUGH"
}