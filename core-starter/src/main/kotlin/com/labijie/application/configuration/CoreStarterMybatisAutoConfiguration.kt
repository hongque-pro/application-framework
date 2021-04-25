package com.labijie.application.configuration

import com.labijie.application.data.mapper.RegionProvinceMapper
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Configuration

/**
 *
 * @author lishiwen
 * @date 19-12-17
 * @since JDK1.8
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(CoreStarterAutoConfiguration::class)
@MapperScan(basePackageClasses = [RegionProvinceMapper::class])
class CoreStarterMybatisAutoConfiguration