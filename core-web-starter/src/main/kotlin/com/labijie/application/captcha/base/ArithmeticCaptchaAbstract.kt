/**
 * @author Anders Xiao
 * @date 2025/7/4
 */

package com.labijie.application.captcha.base


/**
 * 算术验证码抽象类
 * Created by 王帆 on 2019-08-23 上午 10:08.
 */
abstract class ArithmeticCaptchaAbstract : Captcha() {
    private var arithmeticString: String? = null // 计算公式

    init {
        len = 2
    }


    /**
     * 生成随机验证码
     *
     * @return 验证码字符数组
     */
    override fun alphas(): CharArray {
        val sb = StringBuilder()
        for (i in 0..<len) {
            sb.append(num(10))
            if (i < len - 1) {
                val type = num(1, 4)
                if (type == 1) {
                    sb.append("+")
                } else if (type == 2) {
                    sb.append("-")
                } else if (type == 3) {
                    sb.append("x")
                }
            }
        }

        //        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine engine = manager.getEngineByName("javascript");
//        try {
//            chars = String.valueOf(engine.eval(sb.toString().replaceAll("x", "*")));
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
        val result = Calculator.conversion(sb.toString().replace("x".toRegex(), "*")).toInt()
        this.chars = result.toString()

        sb.append("=?")
        arithmeticString = sb.toString()
        return chars!!.toCharArray()
    }

    fun getArithmeticString(): String {
        checkAlpha()
        return arithmeticString.orEmpty()
    }

    fun setArithmeticString(arithmeticString: String?) {
        this.arithmeticString = arithmeticString
    }
}
