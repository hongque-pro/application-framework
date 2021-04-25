package com.labijie.application.auth.configuration

import com.labijie.application.auth.data.mapper.UserMapper
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Configuration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MybatisAutoConfiguration::class)
@AutoConfigureBefore(AuthServerAutoConfiguration::class)
@MapperScan(basePackageClasses = [UserMapper::class])
class AuthServerMybatisAutoConfiguration