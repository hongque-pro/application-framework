package com.labijie.application.payment.car.scene

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 18:24
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */

/**
 * 表示一个车辆支付场景抽象
 */
abstract class CarSceneBase {
    @NotBlank
    @Length(max=8)
    var carNumber:String = ""
}