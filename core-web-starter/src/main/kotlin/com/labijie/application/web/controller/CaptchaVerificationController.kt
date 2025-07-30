package com.labijie.application.web.controller

import com.labijie.application.component.IHumanChecker
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.toSimpleValue
import com.labijie.application.web.InvalidRequestArgumentsException
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


/**
 * @author Anders Xiao
 * @date 2023-12-03
 */

@PermitAll
@RestController
@Validated
@RequestMapping("/captcha")
class CaptchaVerificationController(
    private val captchaHumanChecker: IHumanChecker
) {
    @PostMapping("/verify")
    fun verify(
        @RequestParam(required = true) @NotBlank code: String,
        @RequestParam(required = false) stamp: String? = null,
        request: HttpServletRequest): SimpleValue<Boolean> {

        if(stamp.isNullOrBlank() && captchaHumanChecker.clientStampRequired()){
            throw InvalidRequestArgumentsException("stamp", "Parameter 'stamp' is required for human checking.")
        }

        if(code.isBlank()) {
            return false.toSimpleValue()
        }
        val valid = captchaHumanChecker.check(code, stamp, request.remoteAddr)
        return valid.toSimpleValue()
    }

}