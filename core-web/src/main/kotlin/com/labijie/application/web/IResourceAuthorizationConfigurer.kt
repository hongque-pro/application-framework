package com.labijie.application.web

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-14
 */
interface IResourceAuthorizationConfigurer{
    fun configure(registry: ResourceAuthorizationRegistry)
}