package ac.github.oa.internal.core.item.script.hoop.ternary

import java.math.BigDecimal

interface CompareSymbol {

    val name: String

    fun apply(decimal1: BigDecimal, decimal2: BigDecimal): Boolean

}
