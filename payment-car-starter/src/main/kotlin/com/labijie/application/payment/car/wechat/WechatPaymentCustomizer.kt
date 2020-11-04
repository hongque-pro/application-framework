package com.labijie.application.payment.car.wechat

import com.labijie.application.payment.car.wechat.scene.CarUserGuideSceneSupport
import com.labijie.application.payment.providers.wechat.IWechatPaymentProviderCustomizer
import com.labijie.application.payment.providers.wechat.WechatPaymentProvider

/**

 * Author: Anders Xiao
 * Date: Created in 2020/2/27 15:28
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink

 */
class WechatPaymentCustomizer : IWechatPaymentProviderCustomizer {
    override fun customize(wechatPaymentProvider: WechatPaymentProvider) {
        wechatPaymentProvider.addSceneSupport(CarUserGuideSceneSupport())
    }
}