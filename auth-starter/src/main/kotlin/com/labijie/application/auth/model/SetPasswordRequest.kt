package com.labijie.application.auth.model

import com.labijie.application.model.SmsAssociated
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-24
 */
class SetPasswordRequest : SmsAssociated() {

    @get:Range(min = 1)
    @get:Range(min = 1)
    var userId:Long = 0

    @get:NotBlank
    @get:Length(min=6)
    var password:String = ""
}