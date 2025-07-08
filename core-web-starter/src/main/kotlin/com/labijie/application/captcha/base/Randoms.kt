package com.labijie.application.captcha.base

import java.security.SecureRandom

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
open class Randoms {
    protected val RANDOM: SecureRandom = SecureRandom()

    // 定义验证码字符.去除了0、O、I、L等容易混淆的字母
    val ALPHA: CharArray? = charArrayOf(
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'J',
        'K',
        'M',
        'N',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'j',
        'k',
        'm',
        'n',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z'
    )

    protected val numMaxIndex: Int = 8 // 数字的最大索引，不包括最大值
    protected val charMinIndex: Int = numMaxIndex // 字符的最小索引，包括最小值
    protected val charMaxIndex: Int = ALPHA!!.size // 字符的最大索引，不包括最大值
    protected val upperMinIndex: Int = charMinIndex // 大写字符最小索引
    protected val upperMaxIndex: Int = upperMinIndex + 23 // 大写字符最大索引
    protected val lowerMinIndex: Int = upperMaxIndex // 小写字母最小索引
    protected val lowerMaxIndex: Int = charMaxIndex // 小写字母最大索引

    /**
     * 产生两个数之间的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    fun num(min: Int, max: Int): Int {
        return min + RANDOM.nextInt(max - min)
    }

    /**
     * 产生0-num的随机数,不包括num
     *
     * @param num 最大值
     * @return 随机数
     */
    fun num(num: Int): Int {
        return RANDOM.nextInt(num)
    }

    /**
     * 返回ALPHA中的随机字符
     *
     * @return 随机字符
     */
    fun alpha(): Char {
        return ALPHA!![num(ALPHA.size)]
    }

    /**
     * 返回ALPHA中第0位到第num位的随机字符
     *
     * @param num 到第几位结束
     * @return 随机字符
     */
    fun alpha(num: Int): Char {
        return ALPHA!![num(num)]
    }

    /**
     * 返回ALPHA中第min位到第max位的随机字符
     *
     * @param min 从第几位开始
     * @param max 到第几位结束
     * @return 随机字符
     */
    fun alpha(min: Int, max: Int): Char {
        return ALPHA!![num(min, max)]
    }
}