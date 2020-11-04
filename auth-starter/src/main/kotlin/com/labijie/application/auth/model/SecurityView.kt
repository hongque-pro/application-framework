package com.labijie.application.auth.model

import com.labijie.application.model.BindingStatus

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-17
 */
data class SecurityView(
    var email:String = "",
    var emialStatus: BindingStatus = BindingStatus.Unbound,
    var mobile:String = "",
    var mobileStatus:BindingStatus = BindingStatus.Unbound
)