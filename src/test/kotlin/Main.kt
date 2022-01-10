import ac.github.oa.internal.attribute.impl.other.AttackSpeed
import ac.github.oa.internal.core.hook.OriginPlaceholder
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val spot = Spot(5000)

            val a = 10

            (0 until 8).forEach {
                val base = if (spot.isValid()) spot.task().apply { println(this) } else 1.0
                println(a * base)
                Thread.sleep(1000)
            }
        }


    }


    class Spot(val survival: Long) {

        val stamp = System.currentTimeMillis()

        fun isValid(): Boolean {
            return surplus() > 0
        }

        fun surplus(): Long {
            return stamp + survival - System.currentTimeMillis()
        }

        fun task(): Double {
            return 1.0 - surplus().toDouble().div(survival.toDouble())
        }
    }


}