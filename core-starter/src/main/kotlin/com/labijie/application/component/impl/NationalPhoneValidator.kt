/**
 * @author Anders Xiao
 * @date 2024-05-28
 */
package com.labijie.application.component.impl

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.labijie.application.component.IPhoneValidator
import com.labijie.application.exception.InvalidPhoneNumberException


class NationalPhoneValidator : IPhoneValidator {

    override fun validate(dialingCode: Short, phoneNumber: String, throwIfInvalid: Boolean): Boolean {
        val number = phoneNumber.toLongOrNull()

        if(number == null ||dialingCode < 0){
            if(throwIfInvalid){
                throw InvalidPhoneNumberException(inputPhone = "$dialingCode $phoneNumber")
            }
            return false
        }

        val num = Phonenumber.PhoneNumber().apply {
            setCountryCode(dialingCode.toInt())
            nationalNumber = number
        }

        var valid = dialingCode == 86.toShort()
        valid = valid && phoneNumber.isNotBlank() && PhoneNumberUtil.getInstance().isValidNumber(num)

        if(!valid && throwIfInvalid){
            throw InvalidPhoneNumberException(inputPhone = "$dialingCode $phoneNumber")
        }

        return valid
    }
}