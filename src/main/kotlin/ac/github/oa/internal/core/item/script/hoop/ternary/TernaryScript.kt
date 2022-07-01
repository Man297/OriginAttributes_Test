package ac.github.oa.internal.core.item.script.hoop.ternary

import ac.github.oa.internal.core.item.script.BaseWrapper
import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import org.bukkit.entity.Entity

object TernaryScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "t"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val split = string.split(" ? ")
        val expression = split[0]
        val compareSymbol = CompareSymbolManager.symbols.firstOrNull { expression.contains(" ${it.name} ") }
            ?: throw IllegalThreadStateException(expression)
        val formula = expression.split(" ${compareSymbol.name} ")
        val apply = compareSymbol.apply(formula[0].toBigDecimal(), formula[1].toBigDecimal())
        val results = split.subList(1, split.size).joinToString(" ? ").split(" : ")
        return if (apply) results[0] else results[1]
    }

}