package com.labijie.application.auth.social.exception

import com.labijie.application.ApplicationRuntimeException

class NoneOpenIdException (appId:String, loginProvider:String)
    : ApplicationRuntimeException("Current user have no open id for this app ( appId: $appId, loginProvider: $loginProvider )") {
}