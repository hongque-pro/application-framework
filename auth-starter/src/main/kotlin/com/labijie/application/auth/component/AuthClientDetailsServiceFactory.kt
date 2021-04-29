package com.labijie.application.auth.component

import com.labijie.infra.oauth2.IClientDetailsServiceFactory
import org.springframework.security.oauth2.provider.ClientDetailsService

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-06
 */
class AuthClientDetailsServiceFactory(
    private val service: AuthClientDetailsService) : IClientDetailsServiceFactory {

    override fun createClientDetailsService(): ClientDetailsService {
        return service
    }
}