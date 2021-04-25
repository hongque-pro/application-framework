package com.labijie.application.geo.configuration

import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Configuration

@AutoConfigureAfter(MybatisAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
@MapperScan(basePackages = ["com.labijie.application.geo.data.custom.mapper"])
class GeoMybatisMapperAutoConfiguration