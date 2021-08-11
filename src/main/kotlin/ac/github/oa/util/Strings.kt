package ac.github.oa.util

import java.lang.StringBuilder
import java.util.ArrayList

object Strings {
    fun readVariables(string: String, p: Char, s: Char): List<String> {
        val chars = string.toCharArray()
        val list: MutableList<String> = ArrayList()
        var start = -1
        for (i in chars.indices) {
            val aChar = chars[i]
            if (aChar == p) {
                start = i
            } else if (aChar == s) {
                list.add(string.substring(start + 1, i))
                start = -1
            }
        }
        return list
    }

    fun parseLowerString(string: String): String {
        val stringBuilder = StringBuilder()
        val chars = string.toCharArray()
        var i = 0
        val charsLength = chars.size
        while (i < charsLength) {
            var aChar = chars[i]
            if (Character.isUpperCase(aChar)) {
                aChar = aChar.lowercaseChar()
                if (i != 0) {
                    stringBuilder.append("-")
                }
            }
            stringBuilder.append(aChar)
            i++
        }
        return stringBuilder.toString()
    }
}