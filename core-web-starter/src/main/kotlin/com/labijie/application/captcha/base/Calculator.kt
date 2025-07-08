/**
 * @author Anders Xiao
 * @date 2025/7/4
 */

package com.labijie.application.captcha.base

import com.labijie.application.captcha.base.ArithHelper.add
import com.labijie.application.captcha.base.ArithHelper.div
import com.labijie.application.captcha.base.ArithHelper.mul
import com.labijie.application.captcha.base.ArithHelper.sub
import java.util.*


/**
 * 字符串计算器
 * @link https://www.cnblogs.com/woider/p/5331391.html
 */
class Calculator {
    private val postfixStack = Stack<String>() // 后缀式栈
    private val opStack = Stack<Char?>() // 运算符栈
    private val operatePriority = intArrayOf(0, 3, 2, 1, -1, 1, 0, 2) // 运用运算符ASCII码-40做索引的运算符优先级

    /**
     * 按照给定的表达式计算
     *
     * @param expression 要计算的表达式例如:5+12*(3+5)/7
     * @return
     */
    fun calculate(expression: String): Double {
        val resultStack = Stack<String>()
        prepare(expression)
        Collections.reverse(postfixStack) // 将后缀式栈反转
        var firstValue: String
        var secondValue: String
        var currentValue: String // 参与计算的第一个值，第二个值和算术运算符
        while (!postfixStack.isEmpty()) {
            currentValue = postfixStack.pop()
            if (!isOperator(currentValue.get(0))) { // 如果不是运算符则存入操作数栈中
                currentValue = currentValue.replace("~", "-")
                resultStack.push(currentValue)
            } else { // 如果是运算符则从操作数栈中取两个值和该数值一起参与运算
                secondValue = resultStack.pop()
                firstValue = resultStack.pop()

                // 将负数标记符改为负号
                firstValue = firstValue.replace("~", "-")
                secondValue = secondValue.replace("~", "-")

                val tempResult = calculate(firstValue, secondValue, currentValue.get(0))
                resultStack.push(tempResult)
            }
        }
        return resultStack.pop().toDouble()
    }

    /**
     * 数据准备阶段将表达式转换成为后缀式栈
     *
     * @param expression
     */
    private fun prepare(expression: String) {
        opStack.push(',') // 运算符放入栈底元素逗号，此符号优先级最低
        val arr = expression.toCharArray()
        var currentIndex = 0 // 当前字符的位置
        var count = 0 // 上次算术运算符到本次算术运算符的字符的长度便于或者之间的数值
        var currentOp: Char
        var peekOp: Char // 当前操作符和栈顶操作符
        for (i in arr.indices) {
            currentOp = arr[i]
            if (isOperator(currentOp)) { // 如果当前字符是运算符
                if (count > 0) {
                    postfixStack.push(String(arr, currentIndex, count)) // 取两个运算符之间的数字
                }
                peekOp = opStack.peek()!!
                if (currentOp == ')') { // 遇到反括号则将运算符栈中的元素移除到后缀式栈中直到遇到左括号
                    while (opStack.peek() != '(') {
                        postfixStack.push(opStack.pop().toString())
                    }
                    opStack.pop()
                } else {
                    while (currentOp != '(' && peekOp != ',' && compare(currentOp, peekOp)) {
                        postfixStack.push(opStack.pop().toString())
                        peekOp = opStack.peek()!!
                    }
                    opStack.push(currentOp)
                }
                count = 0
                currentIndex = i + 1
            } else {
                count++
            }
        }
        if (count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) { // 最后一个字符不是括号或者其他运算符的则加入后缀式栈中
            postfixStack.push(String(arr, currentIndex, count))
        }

        while (opStack.peek() != ',') {
            postfixStack.push(opStack.pop().toString()) // 将操作符栈中的剩余的元素添加到后缀式栈中
        }
    }

    /**
     * 判断是否为算术符号
     *
     * @param c
     * @return
     */
    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')'
    }

    /**
     * 利用ASCII码-40做下标去算术符号优先级
     *
     * @param cur
     * @param peek
     * @return
     */
    fun compare(cur: Char, peek: Char): Boolean { // 如果是peek优先级高于cur，返回true，默认都是peek优先级要低
        var result = false
        if (operatePriority[(peek).code - 40] >= operatePriority[(cur).code - 40]) {
            result = true
        }
        return result
    }

    /**
     * 按照给定的算术运算符做计算
     *
     * @param firstValue
     * @param secondValue
     * @param currentOp
     * @return
     */
    private fun calculate(firstValue: String, secondValue: String, currentOp: Char): String {
        var result = ""
        when (currentOp) {
            '+' -> result = add(firstValue, secondValue).toString()
            '-' -> result = sub(firstValue, secondValue).toString()
            '*' -> result = mul(firstValue, secondValue).toString()
            '/' -> result = div(firstValue, secondValue).toString()
        }
        return result
    }

    companion object {
        fun conversion(expression: String): Double {
            var expression = expression
            var result = 0.0
            val cal = Calculator()
            try {
                expression = transform(expression)
                result = cal.calculate(expression)
            } catch (e: Exception) {
                // e.printStackTrace();
                // 运算错误返回NaN
                return Double.NaN
            }
            // return new String().valueOf(result);
            return result
        }

        /**
         * 将表达式中负数的符号更改
         *
         * @param expression 例如-2+-1*(-3E-2)-(-1) 被转为 ~2+~1*(~3E~2)-(~1)
         * @return
         */
        private fun transform(expression: String): String {
            val arr = expression.toCharArray()
            for (i in arr.indices) {
                if (arr[i] == '-') {
                    if (i == 0) {
                        arr[i] = '~'
                    } else {
                        val c = arr[i - 1]
                        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == 'E' || c == 'e') {
                            arr[i] = '~'
                        }
                    }
                }
            }
            if (arr[0] == '~' || arr[1] == '(') {
                arr[0] = '-'
                return "0" + String(arr)
            } else {
                return String(arr)
            }
        }
    }
}
