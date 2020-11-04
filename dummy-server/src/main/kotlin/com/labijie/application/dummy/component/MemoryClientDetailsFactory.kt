package com.labijie.application.dummy.component

import com.labijie.infra.oauth2.Constants
import com.labijie.infra.oauth2.IClientDetailsServiceFactory
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.stereotype.Component

@Component
class MemoryClientDetailsFactory : IClientDetailsServiceFactory {
    override fun createClientDetailsService(): ClientDetailsService {
        return  InMemoryClientDetailsServiceBuilder().apply {
            withClient("cid")
            .secret("cpwd")
            .scopes("all")
            .authorizedGrantTypes(Constants.GRANT_TYPE_PASSWORD)
            }.build()

    }
}