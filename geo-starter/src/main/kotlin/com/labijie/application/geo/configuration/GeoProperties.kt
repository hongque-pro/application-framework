package com.labijie.application.geo.configuration

import com.labijie.application.geo.CoordinateSystem
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.geo")
class GeoProperties {
    var coordinateSystem: CoordinateSystem = CoordinateSystem.GCJ02
}