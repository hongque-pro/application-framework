package com.labijie.application.captcha

import com.labijie.application.captcha.base.Captcha
import com.labijie.application.captcha.utils.GifEncoder
import java.awt.*
import java.awt.geom.CubicCurve2D
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.OutputStream

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
@Suppress("unused")
class GifCaptcha : Captcha {
    constructor()

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

    public override fun out(os: OutputStream): Boolean {
        try {
            // 获取验证码数组
            val strs: CharArray = textChar()
            // 随机生成每个文字的颜色
            val fontColor: Array<Color?>? = arrayOfNulls<Color>(len)
            for (i in 0..<len) {
                fontColor!![i] = color()
            }
            // 随机生成贝塞尔曲线参数
            val x1 = 5
            var y1: Int = num(5, height / 2)
            val x2: Int = width - 5
            var y2: Int = num(height / 2, height - 5)
            val ctrlx: Int = num(width / 4, width / 4 * 3)
            val ctrly: Int = num(5, height - 5)
            if (num(2) == 0) {
                val ty = y1
                y1 = y2
                y2 = ty
            }
            val ctrlx1: Int = num(width / 4, width / 4 * 3)
            val ctrly1: Int = num(5, height - 5)
            val besselXY = arrayOf<IntArray?>(
                intArrayOf(x1, y1),
                intArrayOf(ctrlx, ctrly),
                intArrayOf(ctrlx1, ctrly1),
                intArrayOf(x2, y2)
            )
            // 开始画gif每一帧
            val gifEncoder = GifEncoder()
            gifEncoder.setQuality(180)
            gifEncoder.setDelay(100)
            gifEncoder.setRepeat(0)
            gifEncoder.start(os)
            for (i in 0..<len) {
                val frame = graphicsImage(fontColor!!, strs, i, besselXY)
                gifEncoder.addFrame(frame)
                frame.flush()
            }
            gifEncoder.finish()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    override val mimeType: String
        get() = "image/gif"

    public override fun toBase64(): String {
        return toBase64("data:image/gif;base64,")
    }

    /**
     * 画随机码图
     *
     * @param fontColor 随机字体颜色
     * @param strs      字符数组
     * @param flag      透明度
     * @param besselXY  干扰线参数
     * @return BufferedImage
     */
    private fun graphicsImage(
        fontColor: Array<Color?>,
        strs: CharArray,
        flag: Int,
        besselXY: Array<IntArray?>
    ): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2d = image.graphics as Graphics2D
        // 填充背景颜色
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, width, height)
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        // 画干扰圆圈
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * num(10)) // 设置透明度
        drawOval(2, g2d)
        // 画干扰线
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f) // 设置透明度
        g2d.stroke = BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)
        g2d.color = fontColor[0]
        val shape: CubicCurve2D = CubicCurve2D.Double(
            besselXY[0]!![0].toDouble(),
            besselXY[0]!![1].toDouble(),
            besselXY[1]!![0].toDouble(),
            besselXY[1]!![1].toDouble(),
            besselXY[2]!![0].toDouble(),
            besselXY[2]!![1].toDouble(),
            besselXY[3]!![0].toDouble(),
            besselXY[3]!![1].toDouble()
        )
        g2d.draw(shape)
        // 画验证码
        g2d.font = this.font
        val fontMetrics = g2d.fontMetrics
        val fW: Int = width / strs.size // 每一个字符所占的宽度
        val fSp = (fW - fontMetrics.getStringBounds("W", g2d).getWidth().toInt()) / 2 // 字符的左右边距
        for (i in strs.indices) {
            // 设置透明度
            val ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha(flag, i))
            g2d.composite = ac3
            g2d.color = fontColor[i]
            val fY: Int = height - ((height - fontMetrics.getStringBounds(strs[i].toString(), g2d).height
                .toInt()) shr 1) // 文字的纵坐标
            g2d.drawString(strs[i].toString(), i * fW + fSp + 3, fY - 3)
        }
        g2d.dispose()
        return image
    }

    /**
     * 获取透明度,从0到1,自动计算步长
     *
     * @param i
     * @param j
     * @return 透明度
     */
    private fun getAlpha(i: Int, j: Int): Float {
        val num = i + j
        val r: Float = 1f / (len - 1)
        val s: Float = len * r
        return if (num >= len) (num * r - s) else num * r
    }
}
