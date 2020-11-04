package com.labijie.application.payment.car.scene

import com.labijie.application.payment.DeductMode
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import kotlin.math.max

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 18:58
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

/**
 * 表示一个停车支付的场景
 */
class CarParkingScene : CarSceneBase() {

    /**
     * 平台停车场 id
     */
    @Length(max=64)
    var platformParkingId:String? = null

    /**
     * 入场时间
     */
    var timeEnter:Long = 0

    /**
     * 出场时间
     */
    var timeExit:Long = 0

    /**
     * 停车场名称
     */
    @NotBlank
    @Length(max=32)
    var parkingName:String = ""

    /**
     * 扣款方式：主动/代扣
     */
    var deductMode:DeductMode = DeductMode.WITHHOLDING

    /**
     * 卖家用户号(现用于支付宝)
     */
    var platformSellerId: String? = null

    /**
     * 我方停车场id(现用于支付宝)
     */
    var parkingId: Long? = null
}