package ac.github.oa.internal.core.hook

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.AttributeManager
import org.bukkit.entity.Player
import taboolib.platform.BukkitPlugin
import taboolib.platform.compat.PlaceholderExpansion
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat
import java.util.*

object OriginPlaceholder : PlaceholderExpansion {

    init {
        BukkitPlugin.getInstance().logger.info("|- PlaceholderAPI plugin hooked.")
    }

    val df2 = DecimalFormat("0.00");

    override val identifier: String
        get() = "rpg"


    fun abridge(double: Double, unitType: UnitType): String {
        if (double < unitType.value) return double.toLong().toString()
        return BigDecimal(double, MathContext.DECIMAL128)
            .divide(unitType.value.toBigDecimal())
            .setScale(1,BigDecimal.ROUND_DOWN)
            .toPlainString() + unitType.suffix
    }

    override fun onPlaceholderRequest(p: Player?, params: String): String {
        val split = params.split(":").toTypedArray()
        val key = split[0]
        val args = if (split.size == 1) "" else split[1]
        val attributeData = OriginAttributeAPI.getAttributeData(p!!)

        if (params == "combat-power") {
            return attributeData.combatPower.toString()
        } else if (params == "combat-power:k") {
            return abridge(attributeData.combatPower, UnitType.K)
        } else if (params == "combat-power:w") {
            return abridge(attributeData.combatPower, UnitType.W)
        } else if (params == "health") {
            return df2.format(p.health)
        } else if (params == "max-health") {
            return df2.format(p.maxHealth)
        }


        val attribute = AttributeManager.usableAttributes.values.firstOrNull { it.toName() == key }
        if (attribute !== null) {
            val entry = attribute.getEntry(split[1])
            val data = attributeData.getData(attribute.getPriority(),entry.index)

            entry.toValue(p, if (split.size == 3) split[2] else "",data)?.apply {
                return if (this is Double) {
                    OriginAttribute.decimalFormat.format(this)
                } else this.toString()
            }
        }
        return "N/O"
    }

    enum class UnitType(val value: Double, val suffix: String) {
        K(1000.0, "k"), W(10000.0, "w")
    }
}
