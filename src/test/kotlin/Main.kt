import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import com.google.gson.GsonBuilder
import java.text.DateFormat

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val baseDouble = BaseDouble()
            baseDouble[ValueType.NUMBER] = 12.0
            baseDouble[ValueType.PERCENT] = -90.0
            val number = baseDouble.number()
            println(number)
        }

        fun getNumber(lore: String, valueType: ValueType): BaseDouble {
            var str = lore.replace("§+[a-z0-9]".toRegex(), "")
                .replace("-[^0-9]".toRegex(), "")
                .replace("[^-0-9.%?]".toRegex(), "")
            val s = if (str.isEmpty() || str.replace("[^.]".toRegex(), "").length > 1) "0.0" else str
            val baseDouble = BaseDouble()
            if (str.isEmpty()) return baseDouble
            val lastChar = s[str.length - 1]
            val percent = lastChar == '%'
            if (percent) {
                str = str.substring(0, str.length - 1)
            }
            val valueOf = java.lang.Double.valueOf(str)
            if (valueType === ValueType.NUMBER) {
                if (percent) {
                    baseDouble.put(ValueType.PERCENT, valueOf)
                } else {
                    baseDouble.put(ValueType.NUMBER, valueOf)
                }
            } else if (valueType === ValueType.PERCENT) {
                baseDouble[ValueType.NUMBER] = valueOf
            } else {
                baseDouble[if (percent) ValueType.PERCENT else ValueType.NUMBER] = valueOf
            }
            return baseDouble
        }

    }

}