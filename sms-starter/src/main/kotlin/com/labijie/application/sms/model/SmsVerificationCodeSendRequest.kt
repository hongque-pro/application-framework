/**
 * @author Anders Xiao
 * @date 2025-07-30
 */
package com.labijie.application.sms.model

import com.labijie.application.model.VerificationCodeType

class SmsVerificationCodeSendRequest {
    var dialingCode: Short = 86
    var phoneNumber: String = ""
    var type: VerificationCodeType = VerificationCodeType.General
}
