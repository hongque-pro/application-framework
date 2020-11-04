package com.labijie.application.geo

class MapPointWithDistance(
    id:Long,
    pointName:String = "",
    pointType:Int = 0,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    var distanceMeters:Double = 0.0) : MapPoint(id, pointName, pointType, latitude, longitude)