package com.labijie.application.payment.car.wechat.scene

import com.labijie.application.payment.TradeMode
import com.labijie.application.payment.providers.wechat.IWechatPaymentSceneSupport
import com.labijie.application.payment.providers.wechat.WechatPaymentContext
import com.labijie.application.payment.scene.TradeParameterEffect
import com.labijie.application.payment.car.scene.CarUserGuideScene

/**

 * Author: Anders Xiao
 * Date: Created in 2020/2/27 15:16
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink

 */

class CarUserGuideSceneSupport : IWechatPaymentSceneSupport {
    override fun effectTradeParameters(context: WechatPaymentContext, effect: TradeParameterEffect) {
        //参考：https://pay.weixin.qq.com/wiki/doc/api/vehicle_v2_sl.php?chapter=20_100&index=13&p=202
        val sceneData = effect.input.scene as CarUserGuideScene
        //服务商模式应该使用 subAppId
        val appid = if(context.tradeMode == TradeMode.ISV) context.options.subAppId else context.options.appId

        val existed = effect.output.getOrDefault("attach", "")
        val sceneStringValue = "#*#{\"pn\":\"${sceneData.carNumber}\",\"aid\":\"$appid\"}#*#"
        effect.output["attach"] = "$sceneStringValue$existed"
    }

    override fun isSupported(sceneData: Any): Boolean {
        return sceneData is CarUserGuideScene
    }
}