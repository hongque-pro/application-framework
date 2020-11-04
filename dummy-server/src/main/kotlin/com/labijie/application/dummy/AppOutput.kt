package com.labijie.application.dummy

import com.labijie.application.IModuleInitializer
import com.labijie.infra.utils.logger
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.stereotype.Component

@Component
class AppOutput: IModuleInitializer{
    fun initialize(jwtAccessTokenConverter: JwtAccessTokenConverter){
        logger.warn("jwt-key:${jwtAccessTokenConverter.key}")
    }
}