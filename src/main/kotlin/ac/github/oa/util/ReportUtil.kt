package ac.github.oa.util

import java.util.*
import java.util.regex.Pattern

/**
 * Created by dwtedx(qinyl)(http://dwtedx.com) on 16/9/2.
 * Company DD博客
 * Description java字符串加法运算
 */
object ReportUtil {
    @Throws(Exception::class)
    private fun doubleCal(a1: Double, a2: Double, operator: Char): Double {
        when (operator) {
            '+' -> return a1 + a2
            '-' -> return a1 - a2
            '*' -> return a1 * a2
            '/' -> return a1 / a2
            else -> {
            }
        }
        throw Exception("illegal operator!")
    }

    @Throws(Exception::class)
    private fun getPriority(s: String?): Int {
        if (s == null) return 0
        when (s) {
            "(" -> return 1
            "+" -> {
                return 2
            }
            "-" -> return 2
            "*" -> {
                return 3
            }
            "/" -> return 3
            else -> {
            }
        }
        throw Exception("illegal operator!")
    }

    @Throws(Exception::class)
    private fun toSufExpr(expr: String): String {
        /*返回结果字符串*/
        val sufExpr = StringBuffer()
        /*盛放运算符的栈*/
        val operator = Stack<String?>()
        operator.push(null) //在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断
        /* 将expr打散分散成运算数和运算符 */
        val p = Pattern.compile("(?<!\\d)-?\\d+(\\.\\d+)?|[+\\-*/()]") //这个正则为匹配表达式中的数字或运算符
        val m = p.matcher(expr)
        while (m.find()) {
            val temp = m.group()
            if (temp.matches(Regex("[+\\-*/()]"))) { //是运算符
                if (temp == "(") { //遇到左括号，直接压栈
                    operator.push(temp)
                } else if (temp == ")") { //遇到右括号，弹栈输出直到弹出左括号（左括号不输出）
                    var topItem: String? = null
                    while (operator.pop().also { topItem = it } != "(") {
                        sufExpr.append(topItem).append(" ")
                    }
                } else { //遇到运算符，比较栈顶符号，若该运算符优先级大于栈顶，直接压栈；若小于栈顶，弹栈输出直到大于栈顶，然后将改运算符压栈。
                    while (getPriority(temp) <= getPriority(operator.peek())) {
                        sufExpr.append(operator.pop()).append(" ")
                    }
                    operator.push(temp)
                }
            } else { //遇到数字直接输出
                sufExpr.append(temp).append(" ")
            }
        }
        var topItem: String? = null //最后将符合栈弹栈并输出
        while (null != operator.pop().also { topItem = it }) {
            sufExpr.append(topItem).append(" ")
        }
        return sufExpr.toString()
    }

    fun getResult(expr: String): Double {
        val sufExpr = toSufExpr(expr) // 转为后缀表达式
        /* 盛放数字栈 */
        val number = Stack<Double>()
        /* 这个正则匹配每个数字和符号 */
        val p = Pattern.compile("-?\\d+(\\.\\d+)?|[+\\-*/]")
        val m = p.matcher(sufExpr)
        while (m.find()) {
            val temp = m.group()
            if (temp.matches(Regex("[+\\-*/]"))) { // 遇到运算符，将最后两个数字取出，进行该运算，将结果再放入容器
                val a1 = number.pop()
                val a2 = number.pop()
                val res = doubleCal(a2, a1, temp[0])
                number.push(res)
            } else { // 遇到数字直接放入容器
                number.push(java.lang.Double.valueOf(temp))
            }
        }
        return number.pop() ?: 0.0
    }
}