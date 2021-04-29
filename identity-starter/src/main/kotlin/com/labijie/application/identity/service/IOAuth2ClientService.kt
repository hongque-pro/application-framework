package com.labijie.application.identity.service

import com.labijie.application.identity.data.OAuth2ClientDetailsRecord
import org.springframework.security.crypto.password.PasswordEncoder

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:55
 * @Description:
 */
interface IOAuth2ClientService {
    fun getById(clientId: String): OAuth2ClientDetailsRecord?
    fun getAll(): List<OAuth2ClientDetailsRecord>

    fun setPasswordEncoder(passwordEncoder: PasswordEncoder)
    fun add(clientDetails: OAuth2ClientDetailsRecord)
    fun remove(clientId: String)
    fun update(clientDetails: OAuth2ClientDetailsRecord)
    fun updateSecret(clientId: String, secret: String)
}