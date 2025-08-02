package com.labijie.application.auth.model

import jakarta.validation.constraints.NotNull

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
class ChangePhoneRequest {
    @get:NotNull
    var newPhone: VerifiedPhone = VerifiedPhone()

    var password: String? = null
}




