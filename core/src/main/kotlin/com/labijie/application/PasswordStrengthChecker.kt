package com.labijie.application

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-17
 */

/**
 * 检测密码强度
 *
 */
object PasswordStrengthChecker {

    object StringUtils {

        private val SIZE_TABLE =
            intArrayOf(9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE)

        /**
         * calculate the size of an integer number
         *
         * @param x
         * @return
         */
        fun sizeOfInt(x: Int): Int {
            var i = 0
            while (true) {
                if (x <= SIZE_TABLE[i]) {
                    return i + 1
                }
                i++
            }
        }

        /**
         * Judge whether each character of the string equals
         *
         * @param str
         * @return
         */
        fun isCharEqual(str: String): Boolean {
            return str.replace(str[0], ' ').trim { it <= ' ' }.isEmpty()
        }

        /**
         * Determines if the string is a digit
         *
         * @param str
         * @return
         */
        fun isNumeric(str: String): Boolean {
            var i = str.length
            while (--i >= 0) {
                if (!Character.isDigit(str[i])) {
                    return false
                }
            }
            return true
        }

    }

    /**
     * NUM 数字
     * SMALL_LETTER 小写字母
     * CAPITAL_LETTER 大写字母
     * OTHER_CHAR  特殊字符
     */
    private const val NUM = 1
    private const val SMALL_LETTER = 2
    private const val CAPITAL_LETTER = 3
    private const val OTHER_CHAR = 4

    /**
     * 简单的密码字典
     */
    private val DICTIONARY = arrayOf(
        "password",
        "abc123",
        "iloveyou",
        "adobe123",
        "123123",
        "sunshine",
        "1314520",
        "a1b2c3",
        "123qwe",
        "aaa111",
        "qweasd",
        "admin",
        "passwd"
    )

    enum class LEVEL {
        EASY, MEDIUM, STRONG, VERY_STRONG, EXTREMELY_STRONG
    }

    /**
     * 检查字符类型，包括num、大写字母、小写字母和其他字符。
     *
     * @param c
     * @return
     */
    private fun checkCharacterType(c: Char): Int {
        if (c.code in 48..57) {
            return NUM
        }
        if (c.code in 65..90) {
            return CAPITAL_LETTER
        }
        return if (c.code in 97..122) {
            SMALL_LETTER
        } else OTHER_CHAR
    }

    /**
     * 按不同类型计算密码的数量
     *
     * @param passwd
     * @param type
     * @return
     */
    private fun countLetter(passwd: String?, type: Int): Int {
        var count = 0
        if (!passwd.isNullOrEmpty()) {
            for (c in passwd.toCharArray()) {
                if (checkCharacterType(c) == type) {
                    count++
                }
            }
        }
        return count
    }

    /**
     * 检查密码的强度
     *
     * @param passwd
     * @return strength level
     */
    private fun checkPasswordStrength(passwd: String): Int {
        require(passwd.isEmpty()) { "password is empty" }
        val len = passwd.length
        var level = 0

        // 增加点
        //判断密码是否含有数字有level++
        if (countLetter(passwd, NUM) > 0) {
            level++
        }
        //判断密码是否含有小写字母有level++
        if (countLetter(passwd, SMALL_LETTER) > 0) {
            level++
        }
        //判断密码是否还有大写字母有level++
        if (len > 4 && countLetter(passwd, CAPITAL_LETTER) > 0) {
            level++
        }
        //判断密码是否还有特殊字符有level++
        if (len > 6 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++
        }
        //密码长度大于4并且2种类型组合......（不一一概述）
        if (len > 4 && countLetter(passwd, NUM) > 0 && countLetter(passwd, SMALL_LETTER) > 0
            || countLetter(passwd, NUM) > 0 && countLetter(passwd, CAPITAL_LETTER) > 0
            || countLetter(passwd, NUM) > 0 && countLetter(passwd, OTHER_CHAR) > 0
            || countLetter(passwd, SMALL_LETTER) > 0 && countLetter(passwd, CAPITAL_LETTER) > 0
            || countLetter(passwd, SMALL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0
            || countLetter(passwd, CAPITAL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0
        ) {
            level++
        }
        //密码长度大于6并且3中类型组合......（不一一概述）
        if ((len > 6 && countLetter(passwd, NUM) > 0 && countLetter(passwd, SMALL_LETTER) > 0
                    && countLetter(passwd, CAPITAL_LETTER) > 0) || (countLetter(passwd, NUM) > 0
                    && countLetter(passwd, SMALL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0)
            || (countLetter(passwd, NUM) > 0 && countLetter(passwd, CAPITAL_LETTER) > 0
                    && countLetter(passwd, OTHER_CHAR) > 0) || (countLetter(passwd, SMALL_LETTER) > 0
                    && countLetter(passwd, CAPITAL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0)
        ) {
            level++
        }
        //密码长度大于8并且4种类型组合......（不一一概述）
        if (len > 8 && countLetter(passwd, NUM) > 0 && countLetter(passwd, SMALL_LETTER) > 0
            && countLetter(passwd, CAPITAL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0
        ) {
            level++
        }
        //密码长度大于6并且2种类型组合每种类型长度大于等于3或者2......（不一一概述）
        if (len > 6 && countLetter(passwd, NUM) >= 3 && countLetter(passwd, SMALL_LETTER) >= 3
            || countLetter(passwd, NUM) >= 3 && countLetter(passwd, CAPITAL_LETTER) >= 3
            || countLetter(passwd, NUM) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2
            || countLetter(passwd, SMALL_LETTER) >= 3 && countLetter(passwd, CAPITAL_LETTER) >= 3
            || countLetter(passwd, SMALL_LETTER) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2
            || countLetter(passwd, CAPITAL_LETTER) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2
        ) {
            level++
        }
        //密码长度大于8并且3种类型组合每种类型长度大于等于3或者2......（不一一概述）
        if ((len > 8 && countLetter(passwd, NUM) >= 2 && countLetter(passwd, SMALL_LETTER) >= 2
                    && countLetter(passwd, CAPITAL_LETTER) >= 2) || (countLetter(passwd, NUM) >= 2
                    && countLetter(passwd, SMALL_LETTER) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2)
            || (countLetter(passwd, NUM) >= 2 && countLetter(passwd, CAPITAL_LETTER) >= 2
                    && countLetter(passwd, OTHER_CHAR) >= 2) || (countLetter(passwd, SMALL_LETTER) >= 2
                    && countLetter(passwd, CAPITAL_LETTER) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2)
        ) {
            level++
        }
        //密码长度大于10并且4种类型组合每种类型长度大于等于2......（不一一概述）
        if (len > 10 && countLetter(passwd, NUM) >= 2 && countLetter(passwd, SMALL_LETTER) >= 2
            && countLetter(passwd, CAPITAL_LETTER) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2
        ) {
            level++
        }
        //特殊字符>=3 level++;
        if (countLetter(passwd, OTHER_CHAR) >= 3) {
            level++
        }
        //特殊字符>=6 level++;
        if (countLetter(passwd, OTHER_CHAR) >= 6) {
            level++
        }
        //长度>12 >16 level++
        if (len > 12) {
            level++
            if (len >= 16) {
                level++
            }
        }

        // 减少点
        if ("abcdefghijklmnopqrstuvwxyz".indexOf(passwd) > 0 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(passwd) > 0) {
            level--
        }
        if ("qwertyuiop".indexOf(passwd) > 0 || "asdfghjkl".indexOf(passwd) > 0 || "zxcvbnm".indexOf(passwd) > 0) {
            level--
        }
        if (StringUtils.isNumeric(passwd) && ("01234567890".indexOf(passwd) > 0 || "09876543210".indexOf(passwd) > 0)) {
            level--
        }

        if (countLetter(passwd, NUM) == len || countLetter(passwd, SMALL_LETTER) == len
            || countLetter(passwd, CAPITAL_LETTER) == len
        ) {
            level--
        }

        if (len % 2 == 0) { // aaabbb
            val part1 = passwd.substring(0, len / 2)
            val part2 = passwd.substring(len / 2)
            if (part1 == part2) {
                level--
            }
            if (StringUtils.isCharEqual(part1) && StringUtils.isCharEqual(part2)) {
                level--
            }
        }
        if (len % 3 == 0) { // ababab
            val part1 = passwd.substring(0, len / 3)
            val part2 = passwd.substring(len / 3, len / 3 * 2)
            val part3 = passwd.substring(len / 3 * 2)
            if (part1 == part2 && part2 == part3) {
                level--
            }
        }

        if (StringUtils.isNumeric(passwd) && len >= 6) { // 19881010 or 881010
            var year = 0
            if (len == 8 || len == 6) {
                year = Integer.parseInt(passwd.substring(0, len - 4))
            }
            val size = StringUtils.sizeOfInt(year)
            val month = Integer.parseInt(passwd.substring(size, size + 2))
            val day = Integer.parseInt(passwd.substring(size + 2, len))
            if (year in 1950..2049 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                level--
            }
        }

        if (DICTIONARY.isNotEmpty()) {// dictionary
            for (i in DICTIONARY.indices) {
                if (passwd == DICTIONARY[i] || DICTIONARY[i].indexOf(passwd) >= 0) {
                    level--
                    break
                }
            }
        }

        if (len <= 6) {
            level--
            if (len <= 4) {
                level--
                if (len <= 3) {
                    level = 0
                }
            }
        }

        if (StringUtils.isCharEqual(passwd)) {
            level = 0
        }

        if (level < 0) {
            level = 0
        }

        return level
    }

    /**
     * 获得密码强度等级，包括简单、复杂、强、强、强
     *
     * @param password
     * @return
     */
    fun getPasswordLevel(password: String): LEVEL {
        val level = checkPasswordStrength(password)
        return when (level) {
            0, 1, 2, 3 -> LEVEL.EASY
            4, 5, 6 -> LEVEL.MEDIUM
            7, 8, 9 -> LEVEL.STRONG
            10, 11, 12 -> LEVEL.VERY_STRONG
            else -> LEVEL.EXTREMELY_STRONG
        }
    }
}