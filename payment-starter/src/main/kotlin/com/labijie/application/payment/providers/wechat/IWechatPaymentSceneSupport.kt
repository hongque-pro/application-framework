package com.labijie.application.payment.providers.wechat

import com.labijie.application.payment.scene.ISceneSupport
import com.labijie.application.payment.scene.TradeParameterEffect

/**
 * 微信支付的场景支持
 */
interface IWechatPaymentSceneSupport : ISceneSupport {
    /**
     * 作用于统一下单 API 的调用参数。
     */
    fun effectTradeParameters(context:WechatPaymentContext, effect: TradeParameterEffect)
}