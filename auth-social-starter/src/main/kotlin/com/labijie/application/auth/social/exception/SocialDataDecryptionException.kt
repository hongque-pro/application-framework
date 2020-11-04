package com.labijie.application.auth.social.exception

import com.labijie.application.auth.social.AuthSocialErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class SocialDataDecryptionException(providerName:String, message: String? = null) :
    AuthSocialException(providerName, AuthSocialErrors.DATA_DECRYPTION_FAULT, message ?: "data decryption fault")