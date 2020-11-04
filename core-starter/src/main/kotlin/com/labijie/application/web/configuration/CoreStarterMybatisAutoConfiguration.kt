package com.labijie.application.web.configuration

import com.labijie.application.web.data.mapper.RegionProvinceMapper
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Configuration

/**
 *
 * @author lishiwen
 * @date 19-12-17
 * @since JDK1.8
 */
@Configuration
@AutoConfigureBefore(CoreStarterAutoConfiguration::class)
@MapperScan(basePackageClasses = [RegionProvinceMapper::class])
class CoreStarterMybatisAutoConfiguration