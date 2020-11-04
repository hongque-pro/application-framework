package com.labijie.application.geo.testing

import com.labijie.application.geo.configuration.GeoMybatisMapperAutoConfiguration
import com.labijie.application.geo.configuration.GeoServiceAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(GeoMybatisMapperAutoConfiguration::class, GeoServiceAutoConfiguration::class)
class UnitTestConfiguration
