package ac.github.oa.internal.core.script.hoop.ternary

import ac.github.oa.internal.core.script.hoop.ternary.CompareSymbol
import java.math.BigDecimal

object CompareSymbolGE : CompareSymbol {
    override val name: String
        get() = ">="

    override fun apply(decimal1: BigDecimal, decimal2: BigDecimal): Boolean {
        return decimal2.compareTo(decimal1) > -1
    }
}