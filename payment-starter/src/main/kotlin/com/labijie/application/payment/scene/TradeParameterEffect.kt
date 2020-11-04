package com.labijie.application.payment.scene

import com.labijie.application.payment.PlatformTrade



class TradeParameterEffectGeneric<TOutput>(input: PlatformTrade, output: TOutput) :
    InputOutputEffect<PlatformTrade, TOutput>(input, output) {
}

class TradeParameterEffect(input: PlatformTrade, output: MutableMap<String, String>) :
    InputOutputEffect<PlatformTrade, MutableMap<String, String>>(input, output) {
}