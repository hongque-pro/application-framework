package com.labijie.application.geo.data.custom

class GeoPointWithDistanceRecord(
    var id:Long,
    var pointName:String = "",
    var pointType:Int = 0,
    var distance:Double = 0.0,
    var mapLatLng: ByteArray? = null
)