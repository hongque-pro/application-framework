package com.labijie.application.open.configuration

import com.labijie.application.open.data.mapper.OpenPartnerMapper
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MybatisAutoConfiguration::class)
@AutoConfigureBefore(OpenApiAutoConfiguration::class)
@MapperScan(basePackageClasses = [OpenPartnerMapper::class])
class OpenApiMybatisAutoConfiguration