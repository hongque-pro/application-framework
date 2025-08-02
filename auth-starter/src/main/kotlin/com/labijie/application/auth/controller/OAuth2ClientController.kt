///**
// * @author Anders Xiao
// * @date 2024-06-12
// */
//package com.labijie.application.auth.controller
//
//import com.labijie.application.auth.service.IOAuth2ClientUserService
//import com.labijie.application.auth.service.getUserByOAuth2User
//import com.labijie.application.auth.toUserDetails
//import com.labijie.application.exception.UserNotFoundException
//import com.labijie.infra.oauth2.AccessToken
//import com.labijie.infra.oauth2.OAuth2ServerUtils.toAccessToken
//import com.labijie.infra.oauth2.TwoFactorSignInHelper
//import com.labijie.infra.oauth2.client.StandardOidcUser
//import com.labijie.infra.oauth2.configuration.OAuth2ServerProperties
//import jakarta.annotation.security.PermitAll
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//
//@PermitAll
//@RestController
//@RequestMapping("/oauth2/client")
//class OAuth2ClientController(
//    private val oAuth2ServerProperties: OAuth2ServerProperties,
//    private val signInHelper: TwoFactorSignInHelper,
//    private val oauth2ClientUserService: IOAuth2ClientUserService
//) {
//
//    @PostMapping("/exchange-token")
//    fun exchangeToken(token: StandardOidcUser): AccessToken {
//        val user = oauth2ClientUserService.getUserByOAuth2User(token) ?: throw UserNotFoundException()
//
//        val accessToken = signInHelper.signIn(oAuth2ServerProperties.defaultClient.clientId, user.toUserDetails())
//        return accessToken.toAccessToken()
//    }
//
//
//}