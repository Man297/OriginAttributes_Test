package ac.github.oa.internal.core.script.hoop.ternary

import java.math.BigDecimal

object CompareSymbolNE : CompareSymbol {
    override val name: String
        get() = "!="

    override fun apply(decimal1: BigDecimal, decimal2: BigDecimal): Boolean {
        return decimal2.compareTo(decimal1) != 0
    }
}