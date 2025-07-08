package com.labijie.application.captcha

import com.labijie.application.captcha.base.Captcha
import java.awt.*
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.OutputStream
import javax.imageio.ImageIO

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
@Suppress("unused")
class SpecCaptcha() : Captcha() {
    constructor(width: Int, height: Int, len: Int? = null,  font: Font? = null) : this() {
        this.width = width
        this.height = height
        len?.let {
            this.len = it
        }
        font?.let {
            this.font = font
        }
    }

    /**
     * 生成验证码
     *
     * @param os 输出流
     * @return 是否成功
     */
    public override fun out(os: OutputStream): Boolean {
        return graphicsImage(textChar(), os)
    }

    override val mimeType: String
        get() = "image/png"

    public override fun toBase64(): String {
        return toBase64("data:${mimeType};base64,")
    }

    /**
     * 生成验证码图形
     *
     * @param strs 验证码
     * @param out  输出流
     * @return boolean
     */
    private fun graphicsImage(strs: CharArray, out: OutputStream): Boolean {
        try {
            val bi = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val g2d = bi.graphics as Graphics2D
            // 填充背景
            g2d.color = Color.WHITE
            g2d.fillRect(0, 0, width, height)
            // 抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            // 画干扰圆
            drawOval(2, g2d)
            // 画干扰线
            g2d.stroke = BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)
            drawBesselLine(1, g2d)
            // 画字符串
            g2d.font = this.font
            val fontMetrics = g2d.fontMetrics
            val fW: Int = width / strs.size // 每一个字符所占的宽度
            val fSp = (fW - fontMetrics.getStringBounds("W", g2d).width.toInt()) / 2 // 字符的左右边距
            for (i in strs.indices) {
                g2d.color = color()
                val fY: Int = height - ((height - fontMetrics.getStringBounds(strs[i].toString(), g2d).height
                    .toInt()) shr 1) // 文字的纵坐标
                g2d.drawString(strs[i].toString(), i * fW + fSp + 3, fY - 3)
            }
            g2d.dispose()
            ImageIO.write(bi, "jpg", out)
            out.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }
}