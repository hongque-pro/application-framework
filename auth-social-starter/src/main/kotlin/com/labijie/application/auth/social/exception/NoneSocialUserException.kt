package com.labijie.application.auth.social.exception

import com.labijie.application.ApplicationRuntimeException

class NoneSocialUserException(message:String = "Current user is not login from social network.") : ApplicationRuntimeException(message) {
}