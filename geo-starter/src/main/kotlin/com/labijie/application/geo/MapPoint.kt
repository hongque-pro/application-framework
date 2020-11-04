package com.labijie.application.geo

open class MapPoint (
    var id:Long,
    var pointName:String = "",
    var pointType:Int = 0,
    override var latitude: Double = 0.0,
    override var longitude: Double = 0.0) :ICoordinate


