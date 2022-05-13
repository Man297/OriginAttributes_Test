package ac.github.oa.internal.core.script.func

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.util.ReportUtil
import org.bukkit.entity.Entity
import taboolib.common.platform.Awake
import taboolib.common5.Coerce

@Awake
class MathMaxScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "max"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val list = string.split(",").map { Coerce.toFloat(it) }
        return list.maxOf { it }.toString()
    }
}

@Awake
class MathMinScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "min"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val list = string.split(",").map { Coerce.toFloat(it) }
        return list.minOf { it }.toString()
    }
}

@Awake
class MathToIntScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "toInt"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        return Coerce.toInteger(string).toString()
    }
}

@Awake
class MathToDoubleScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "toDouble"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        return OriginAttribute.decimalFormat.format(string)
    }
}