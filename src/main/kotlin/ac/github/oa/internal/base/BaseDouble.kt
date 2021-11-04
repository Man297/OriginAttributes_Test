package ac.github.oa.internal.base

import ac.github.oa.internal.base.enums.ValueType
import java.util.function.BiConsumer

class BaseDouble : HashMap<ValueType, Double>() {

    fun merge(baseDouble: BaseDouble): BaseDouble {
        baseDouble.forEach { valueType: ValueType, aDouble: Double ->
            this[valueType] = this.getOrDefault(valueType, 0.0) + aDouble
        }
        return this
    }

    fun number(valueType: ValueType): Double {
        return this.getOrDefault(valueType, 0.0)
    }

    fun number(value: Double = 0.0): Double {
        var aDouble = value + this.getOrDefault(ValueType.NUMBER, 0.0)
        val percent = percent()
        aDouble += aDouble * percent
        return aDouble
    }

    /**
     * 数值和百分型计入算法
     *
     * @param value args
     * @return result
     */
    fun globalEval(value: Double): Double {
        return globalEval(0.0, 0.0, value)
    }

    fun globalEval(addonNumber: Double, addonPercent: Double, value: Double): Double {
        var result = value(ValueType.NUMBER) + addonNumber
        result += value * (percent() + addonPercent / 100)
        return result
    }

    fun eval(value: Double): Double {
        return value * percent(ValueType.NUMBER)
    }

    fun percentEval(value: Double): Double {
        return value * percent()
    }

    fun value(valueType: ValueType): Double {
        return this.getOrDefault(valueType, 0.0)
    }

    @JvmOverloads
    fun percent(valueType: ValueType = ValueType.PERCENT): Double {
        return value(valueType) / 100
    }

    @JvmOverloads
    fun random(value: Double = 0.0): Boolean {
        return Math.random() * 1 < (number(ValueType.NUMBER) + value) / 100
    }
}