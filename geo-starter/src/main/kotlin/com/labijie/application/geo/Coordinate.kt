package com.labijie.application.geo

data class Coordinate (
    override var latitude: Double = 0.0,
    override var longitude: Double = 0.0
) : ICoordinate