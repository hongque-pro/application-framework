package com.labijie.application.web.controller

import com.labijie.application.component.IVerificationCodeService
import com.labijie.application.model.SimpleValue
import com.labijie.application.model.toSimpleValue
import jakarta.annotation.security.PermitAll
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@PermitAll
@RestController
@RequestMapping("/verification-code")
@Validated
class VerificationCodeController(private val service: IVerificationCodeService) {

    @PostMapping("/verify")
    fun verify(@NotBlank @Length(max=6) code: String, @NotBlank token: String): SimpleValue<Boolean> {
        service.verifyCode(code, token, false)
        return true.toSimpleValue()
    }
}