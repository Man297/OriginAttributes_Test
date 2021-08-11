package ac.github.oa.internal.base

import kotlin.Throws
import java.lang.ClassNotFoundException

class Node(var symbol: Char, var prefix: String, var suffix: String) {
    override fun toString(): String {
        return "Node{" +
                "symbol=" + symbol +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                '}'
    }
}