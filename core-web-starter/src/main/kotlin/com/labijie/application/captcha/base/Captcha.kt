/**
 * @author Anders Xiao
 * @date 2025/7/4
 *
 * 原始作者 王帆 on 2018-07-27 上午 10:08.
 */

package com.labijie.application.captcha.base

import java.awt.Color
import java.awt.Font
import java.awt.FontFormatException
import java.awt.Graphics2D
import java.awt.geom.CubicCurve2D
import java.awt.geom.QuadCurve2D
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


abstract class Captcha : Randoms() { // 验证码的字体
    private var fontValue: Font? = null
    var len: Int = 4 // 验证码随机字符长度
    var width: Int = 130 // 验证码显示宽度
    var height: Int = 48 // 验证码显示高度
    var charType = CaptchaType.TYPE_DEFAULT // 验证码类型
    protected var chars: String = "" // 当前验证码

    open val mimeType: String = "image/jpeg"

    /**
     * 生成随机验证码
     *
     * @return 验证码字符数组
     */
    protected open fun alphas(): CharArray {
        val cs = CharArray(len)
        when (charType) {
            CaptchaType.UNKNOWN -> alpha()
            CaptchaType.TYPE_DEFAULT -> alpha()
            CaptchaType.TYPE_ONLY_NUMBER -> alpha(numMaxIndex)
            CaptchaType.TYPE_ONLY_CHAR -> alpha(charMinIndex, charMaxIndex)
            CaptchaType.TYPE_ONLY_UPPER -> alpha(upperMinIndex, upperMaxIndex)
            CaptchaType.TYPE_ONLY_LOWER -> alpha(lowerMinIndex, lowerMaxIndex)
            CaptchaType.TYPE_NUM_AND_UPPER -> alpha(upperMaxIndex)
        }
        chars = String(cs)
        return cs
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc 0-255
     * @param bc 0-255
     * @return 随机颜色
     */
    protected fun color(fc: Int, bc: Int): Color {
        var fc = fc
        var bc = bc
        if (fc > 255) fc = 255
        if (bc > 255) bc = 255
        val r = fc + num(bc - fc)
        val g = fc + num(bc - fc)
        val b = fc + num(bc - fc)
        return Color(r, g, b)
    }

    /**
     * 获取随机常用颜色
     *
     * @return 随机颜色
     */
    protected fun color(): Color {
        val color: IntArray = COLOR[num(COLOR.size)]
        return Color(color[0], color[1], color[2])
    }

    /**
     * 验证码输出,抽象方法，由子类实现
     *
     * @param os 输出流
     * @return 是否成功
     */
    abstract fun out(os: OutputStream): Boolean

    /**
     * 输出base64编码
     *
     * @return base64编码字符串
     */
    abstract fun toBase64(): String

    /**
     * 输出base64编码
     *
     * @param type 编码头
     * @return base64编码字符串
     */
    protected fun toBase64(type: String?): String {
        val outputStream = ByteArrayOutputStream()
        out(outputStream)
        return type + Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }

    /**
     * 获取当前的验证码
     *
     * @return 字符串
     */
    fun text(): String {
        checkAlpha()
        return chars
    }

    /**
     * 获取当前验证码的字符数组
     *
     * @return 字符数组
     */
    fun textChar(): CharArray {
        checkAlpha()
        return chars.toCharArray()
    }

    /**
     * 检查验证码是否生成，没有则立即生成
     */
    fun checkAlpha() {
        if (chars.isBlank()) {
            alphas() // 生成验证码
        }
    }

    /**
     * 随机画干扰线
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    fun drawLine(num: Int, g: Graphics2D) {
        drawLine(num, null, g)
    }

    /**
     * 随机画干扰线
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    fun drawLine(num: Int, color: Color?, g: Graphics2D) {
        for (i in 0..<num) {
            g.color = color ?: color()
            val x1 = num(-10, width - 10)
            val y1 = num(5, height - 5)
            val x2 = num(10, width + 10)
            val y2 = num(2, height - 2)
            g.drawLine(x1, y1, x2, y2)
        }
    }

    /**
     * 随机画干扰圆
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    fun drawOval(num: Int, g: Graphics2D) {
        drawOval(num, null, g)
    }

    /**
     * 随机画干扰圆
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    fun drawOval(num: Int, color: Color?, g: Graphics2D) {
        for (i in 0..<num) {
            g.color = color ?: color()
            val w = 5 + num(10)
            g.drawOval(num(width - 25), num(height - 15), w, w)
        }
    }

    /**
     * 随机画贝塞尔曲线
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    fun drawBesselLine(num: Int, g: Graphics2D) {
        drawBesselLine(num, null, g)
    }

    /**
     * 随机画贝塞尔曲线
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    fun drawBesselLine(num: Int, color: Color?, g: Graphics2D) {
        for (i in 0..<num) {
            g.color = color ?: color()
            val x1 = 5
            var y1 = num(5, height / 2)
            val x2 = width - 5
            var y2 = num(height / 2, height - 5)
            val ctrlx = num(width / 4, width / 4 * 3)
            val ctrly = num(5, height - 5)
            if (num(2) == 0) {
                val ty = y1
                y1 = y2
                y2 = ty
            }
            if (num(2) == 0) {  // 二阶贝塞尔曲线
                val shape: QuadCurve2D = QuadCurve2D.Double()
                shape.setCurve(
                    x1.toDouble(),
                    y1.toDouble(),
                    ctrlx.toDouble(),
                    ctrly.toDouble(),
                    x2.toDouble(),
                    y2.toDouble()
                )
                g.draw(shape)
            } else {  // 三阶贝塞尔曲线
                val ctrlx1 = num(width / 4, width / 4 * 3)
                val ctrly1 = num(5, height - 5)
                val shape: CubicCurve2D = CubicCurve2D.Double(
                    x1.toDouble(),
                    y1.toDouble(),
                    ctrlx.toDouble(),
                    ctrly.toDouble(),
                    ctrlx1.toDouble(),
                    ctrly1.toDouble(),
                    x2.toDouble(),
                    y2.toDouble()
                )
                g.draw(shape)
            }
        }
    }

    var font: Font
        get() {
            if (fontValue == null) {
                try {
                    setFont(FONT_1)
                } catch (_: Exception) {
                    val f = Font("Arial", Font.BOLD, 32)
                    fontValue = f
                }
            }
            return fontValue!!
        }
        set(value) {
            fontValue = value
        }


    @Throws(IOException::class, FontFormatException::class)
    fun setFont(font: Int) {
        setFont(font, 32f)
    }

    @Throws(IOException::class, FontFormatException::class)
    fun setFont(font: Int, size: Float) {
        setFont(font, Font.BOLD, size)
    }

    @Throws(IOException::class, FontFormatException::class)
    fun setFont(font: Int, style: Int, size: Float) {
        this.font = Font.createFont(Font.TRUETYPE_FONT, javaClass.getResourceAsStream("/captcha" + FONT_NAMES[font]))
            .deriveFont(style, size)
    }

    companion object {
        // 常用颜色
        val COLOR: Array<IntArray> = arrayOf<IntArray>(
            intArrayOf(0, 135, 255),
            intArrayOf(51, 153, 51),
            intArrayOf(255, 102, 102),
            intArrayOf(255, 153, 0),
            intArrayOf(153, 102, 0),
            intArrayOf(153, 102, 153),
            intArrayOf(51, 153, 153),
            intArrayOf(102, 102, 255),
            intArrayOf(0, 102, 204),
            intArrayOf(204, 51, 51),
            intArrayOf(0, 153, 204),
            intArrayOf(0, 51, 102)
        )


        // 内置字体
        const val FONT_1: Int = 0
        const val FONT_2: Int = 1
        const val FONT_3: Int = 2
        const val FONT_4: Int = 3
        const val FONT_5: Int = 4
        const val FONT_6: Int = 5
        const val FONT_7: Int = 6
        const val FONT_8: Int = 7
        const val FONT_9: Int = 8
        const val FONT_10: Int = 9

        private val FONT_NAMES: Array<String> = arrayOf<String>(
            "actionj.ttf",
            "epilog.ttf",
            "fresnel.ttf",
            "headache.ttf",
            "lexo.ttf",
            "prefix.ttf",
            "progbot.ttf",
            "ransom.ttf",
            "robot.ttf",
            "scandal.ttf"
        )
    }
}