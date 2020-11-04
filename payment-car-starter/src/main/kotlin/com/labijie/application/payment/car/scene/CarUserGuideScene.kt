package com.labijie.application.payment.car.scene

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank

/**

 * Author: Anders Xiao
 * Date: Created in 2020/2/27 14:23
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink

 */

/**
 * 表示支付过程中开启车主服务场景
 *
 * 仅微信支持
 */
data class CarUserGuideScene(
    @get:NotBlank
    @get:Length(max=8)
    var carNumber:String = ""
)