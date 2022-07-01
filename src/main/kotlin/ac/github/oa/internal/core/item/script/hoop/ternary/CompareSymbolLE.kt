package ac.github.oa.internal.core.item.script.hoop.ternary

import java.math.BigDecimal

object CompareSymbolLE : CompareSymbol {
    override val name: String
        get() = "<="

    override fun apply(decimal1: BigDecimal, decimal2: BigDecimal): Boolean {
        return decimal1.compareTo(decimal2) < 1
    }
}