
package com.labijie.application.payment.car

import com.labijie.application.payment.PlatformTrade
import com.labijie.application.payment.car.scene.CarParkingScene
import com.labijie.application.payment.exception.PaymentSceneMissedException

fun PlatformTrade.getCarScene(providerName:String, trade: PlatformTrade): CarParkingScene {
    val scene =
        trade.scene as? CarParkingScene ?: throw PaymentSceneMissedException(providerName, CarParkingScene::class)
    return scene
}