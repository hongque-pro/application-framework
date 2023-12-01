package com.labijie.application.identity.service

import com.labijie.application.identity.data.pojo.OAuth2Client
import org.springframework.security.crypto.password.PasswordEncoder

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:55
 * @Description:
 */
interface IOAuth2ClientService {
    fun getById(clientId: String): OAuth2Client?
    fun getAll(): List<OAuth2Client>

    fun setPasswordEncoder(passwordEncoder: PasswordEncoder)
    fun add(clientDetails: OAuth2Client)
    fun remove(clientId: String)
    fun update(clientDetails: OAuth2Client)
    fun updateSecret(clientId: String, secret: String)
}