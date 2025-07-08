package com.labijie.application.captcha.utils

import com.labijie.application.captcha.SpecCaptcha
import com.labijie.application.captcha.base.Captcha
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.awt.Font
import java.io.IOException
import java.util.*

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
@Suppress("unused")
object CaptchaUtil {
    private const val SESSION_KEY: String = "captcha"
    private const val DEFAULT_LEN: Int = 4 // 默认长度
    private const val DEFAULT_WIDTH: Int = 130 // 默认宽度
    private const val DEFAULT_HEIGHT: Int = 48 // 默认高度

    /**
     * 输出验证码
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun out(request: HttpServletRequest, response: HttpServletResponse) {
        out(DEFAULT_LEN, request, response)
    }

    /**
     * 输出验证码
     *
     * @param len      长度
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun out(len: Int, request: HttpServletRequest, response: HttpServletResponse) {
        out(DEFAULT_WIDTH, DEFAULT_HEIGHT, len, request, response)
    }

    /**
     * 输出验证码
     *
     * @param width    宽度
     * @param height   高度
     * @param len      长度
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun out(width: Int, height: Int, len: Int, request: HttpServletRequest, response: HttpServletResponse) {
        out(width, height, len, null, request, response)
    }

    /**
     * 输出验证码
     *
     * @param font     字体
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun out(font: Font?, request: HttpServletRequest, response: HttpServletResponse) {
        out(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_LEN, font, request, response)
    }

    /**
     * 输出验证码
     *
     * @param len      长度
     * @param font     字体
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun out(len: Int, font: Font?, request: HttpServletRequest, response: HttpServletResponse) {
        out(DEFAULT_WIDTH, DEFAULT_HEIGHT, len, font, request, response)
    }

    /**
     * 输出验证码
     *
     * @param width    宽度
     * @param height   高度
     * @param len      长度
     * @param font     字体
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun out(
        width: Int,
        height: Int,
        len: Int,
        font: Font?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val specCaptcha = SpecCaptcha(width, height, len, font)
        out(specCaptcha, request, response)
    }


    /**
     * 输出验证码
     *
     * @param captcha  Captcha
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun out(captcha: Captcha, request: HttpServletRequest, response: HttpServletResponse) {
        setHeader(response, captcha.mimeType)
        request.session.setAttribute(SESSION_KEY, captcha.text().lowercase())
        captcha.out(response.outputStream)
    }

    /**
     * 验证验证码
     *
     * @param code    用户输入的验证码
     * @param request HttpServletRequest
     * @return 是否正确
     */
    fun ver(code: String, request: HttpServletRequest): Boolean {
        val captcha = request.session.getAttribute(SESSION_KEY) as String?
        return code.trim { it <= ' ' }.lowercase(Locale.getDefault()) == captcha
    }

    /**
     * 清除session中的验证码
     *
     * @param request HttpServletRequest
     */
    fun clear(request: HttpServletRequest) {
        request.session.removeAttribute(SESSION_KEY)
    }

    /**
     * 设置相应头
     *
     * @param response HttpServletResponse
     */
    fun setHeader(response: HttpServletResponse, mimeType: String) {
        response.contentType = mimeType
        response.setHeader("Pragma", "No-cache")
        response.setHeader("Cache-Control", "no-cache")
        response.setDateHeader("Expires", 0)
    }
}