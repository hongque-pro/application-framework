package com.labijie.application.auth.model

import com.labijie.application.model.BindingStatus
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-17
 */
data class SecurityView(
    var email:String = "",
    var emailStatus: BindingStatus = BindingStatus.Unbound,

    var dialingCode: Short? = null,
    var phoneNumber: String = "",

    @get:Length(min=6)
    @get: NotBlank
    var password: String = "",

    var phoneStatus:BindingStatus = BindingStatus.Unbound
)