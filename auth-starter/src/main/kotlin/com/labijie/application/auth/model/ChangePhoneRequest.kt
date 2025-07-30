package com.labijie.application.auth.model

import com.labijie.application.model.VerificationAssociated
import jakarta.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-19
 */
class ChangePhoneRequest : VerificationAssociated() {
    var dialingCode: Short = 86

    @get:NotBlank
    var phoneNumber: String = ""
}