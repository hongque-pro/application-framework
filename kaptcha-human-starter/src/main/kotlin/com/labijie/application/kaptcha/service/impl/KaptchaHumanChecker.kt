package com.labijie.application.kaptcha.service.impl

import com.labijie.application.component.IHumanChecker
import com.labijie.application.kaptcha.service.IKaptchaService
import com.labijie.application.kaptcha.model.ImageCaptchaVerify
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 *
 * @author lishiwen
 * @date 20-8-21
 * @since JDK1.8
 */
@Component
@ConditionalOnProperty(name = ["application.human-checker.kaptcha.enabled"], havingValue = "true", matchIfMissing = false)
class KaptchaHumanChecker(
    private val kaptchaService: IKaptchaService) : IHumanChecker {

    // token 的格式为： "stamp;captcha;userKey", 用分号分隔
    override fun check(token: String, clientIp: String): Boolean {
        val split = token.split(';')
        if (split.size != 3 || split[0].isBlank() || split[1].isBlank())
            return false
        return kaptchaService.validateImageCaptcha(
            ImageCaptchaVerify(
                split[0],
                split[1],
                split[2]
            )
        )
    }

}