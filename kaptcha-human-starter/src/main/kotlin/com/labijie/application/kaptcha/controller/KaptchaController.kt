package com.labijie.application.kaptcha.controller

import com.labijie.application.kaptcha.service.IKaptchaService
import com.labijie.application.kaptcha.model.ImageCaptchaEntry
import com.labijie.application.web.annotation.ResponseWrapped
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author lishiwen
 * @date 20-8-21
 * @since JDK1.8
 */
@RestController
@ResponseWrapped
@Validated
@RequestMapping("/kaptcha")
class KaptchaController(
    private val kaptchaService: IKaptchaService) {

    @GetMapping("/gen")
    fun genCaptcha(@RequestParam userKey: String): ImageCaptchaEntry {
        return kaptchaService.genImageCaptcha(userKey)
    }
}