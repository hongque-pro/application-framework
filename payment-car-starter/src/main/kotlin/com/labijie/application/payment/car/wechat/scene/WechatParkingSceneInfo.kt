package com.labijie.application.payment.car.wechat.scene

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Author: Anders Xiao
 * Date: Created in 2020/2/27 20:16
 * Copyright: Copyright (c) 2020
 * Github: https://github.com/endink
 */
class WechatParkingSceneInfo {

    @JsonProperty("start_time")
    var startTime:String = ""

    @JsonProperty("end_time")
    var endTime:String = ""

    @JsonProperty("charging_time")
    var chargingTime:String = ""

    @JsonProperty("plate_number")
    var plateNumber:String = ""

    @JsonProperty("car_type")
    var carType:String? = null

    @JsonProperty("parking_name")
    var parkingName:String = ""

    @JsonProperty("deduct_mode")
    var deductMode:String = ""
}