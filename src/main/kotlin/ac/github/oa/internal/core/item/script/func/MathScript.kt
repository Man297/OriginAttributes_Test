package ac.github.oa.internal.core.item.script.func

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.item.script.BaseWrapper
import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import org.bukkit.entity.Entity
import taboolib.common5.Coerce

object MathMaxScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "max"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val list = string.split(",").map { Coerce.toFloat(it) }
        return list.maxOf { it }.toString()
    }
}

object MathMinScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "min"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val list = string.split(",").map { Coerce.toFloat(it) }
        return list.minOf { it }.toString()
    }
}

object MathToIntScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "toInt"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        return Coerce.toInteger(string).toString()
    }
}

object MathToDoubleScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "toDouble"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        return OriginAttribute.decimalFormat.format(string)
    }
}