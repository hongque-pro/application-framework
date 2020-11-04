package com.labijie.application.auth.service

import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationService

/**
 *
 * @author lishiwen
 * @date 19-12-6
 * @since JDK1.8
 */
interface IClientService : ClientDetailsService, ClientRegistrationService