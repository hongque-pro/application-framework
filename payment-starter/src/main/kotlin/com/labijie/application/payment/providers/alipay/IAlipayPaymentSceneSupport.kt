package com.labijie.application.payment.providers.alipay

import com.labijie.application.payment.scene.ISceneSupport
import com.labijie.application.payment.scene.TradeParameterEffect
import com.labijie.application.payment.scene.TradeParameterEffectGeneric

interface IAlipayPaymentSceneSupport : ISceneSupport {
    /**
     * 作用于统一下单 API 的调用参数。
     */
    fun effectTradeParameters(context:AlipayPaymentContext, effect: TradeParameterEffect)

    /**
     * 作用于统一下单 API 中的  extend_params 参数
     */
    fun effectTradeParametersExtendParams(context: AlipayPaymentContext, effect: TradeParameterEffectGeneric<Map<String, Any>>)

    /**
     * 作用于统一下单 API 中的  extend_params 参数
     */
    fun effectTradeParametersBizContent(context: AlipayPaymentContext, effect: TradeParameterEffectGeneric<Map<String, Any>>)
}