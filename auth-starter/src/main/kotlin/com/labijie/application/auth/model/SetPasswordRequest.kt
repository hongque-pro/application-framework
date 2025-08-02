package com.labijie.application.auth.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-24
 */
class SetPasswordRequest {

    @NotBlank
    var identifier: String = ""

    @get:NotBlank
    @get:Length(min=6)
    var password:String = ""
}