package com.labijie.application.web.controller

import com.labijie.application.model.CodeVerificationResult
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.web.interceptor.OneTimeCodeInterceptor
import jakarta.annotation.security.PermitAll
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
@PermitAll
@RestController
@RequestMapping("/totp")
@Validated
class OneTimeCodeController(private val service: IOneTimeCodeService) {

    @PostMapping("/verify")
    fun verify(
        @RequestParam(OneTimeCodeInterceptor.CODE_KEY, required = true) @NotBlank code: String,
        @RequestParam(OneTimeCodeInterceptor.STAMP_KEY, required = true) @NotBlank stamp: String,
    ): CodeVerificationResult {
        val result = service.verifyCode(code, stamp)
        return CodeVerificationResult(result.success)
    }
}