package com.labijie.application.kaptcha.service.impl

import com.google.code.kaptcha.Producer
import com.labijie.application.kaptcha.service.IKaptchaService
import com.labijie.application.kaptcha.model.ImageCaptchaEntry
import com.labijie.application.kaptcha.model.ImageCaptchaVerify
import com.labijie.infra.security.Rfc6238TokenService
import com.labijie.infra.utils.ShortId
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 *
 * @author lishiwen
 * @date 20-8-21
 * @since JDK1.8
 */
@Service
class KaptchaService(
    private val rfc6238TokenService: Rfc6238TokenService,
    private val captchaProducer: Producer) : IKaptchaService {

    override fun genImageCaptcha(userKey: String): ImageCaptchaEntry {
        val stamp = ShortId.newId()
        val capText = rfc6238TokenService.generateCode(stamp.toByteArray(), userKey).toString()
        val bi = captchaProducer.createImage(capText)
        val out = ByteArrayOutputStream()
        ImageIO.write(bi, "jpg", out)
        return ImageCaptchaEntry(
            stamp,
            "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(out.toByteArray())
        )
    }

    override fun validateImageCaptcha(param: ImageCaptchaVerify): Boolean {
        val code = param.captcha.toIntOrNull() ?: return false
        return rfc6238TokenService.validateCode(code, param.stamp.toByteArray(), param.userKey)
    }
}