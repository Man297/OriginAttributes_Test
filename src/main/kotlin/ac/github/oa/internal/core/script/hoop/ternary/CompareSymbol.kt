package ac.github.oa.internal.core.script.hoop.ternary

import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import java.math.BigDecimal

interface CompareSymbol {

    val name: String

    fun apply(decimal1: BigDecimal, decimal2: BigDecimal): Boolean

}
