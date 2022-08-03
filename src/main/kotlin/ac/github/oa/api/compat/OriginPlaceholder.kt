package ac.github.oa.api.compat

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.AttributeManager
import org.bukkit.entity.Player
import taboolib.platform.BukkitPlugin
import taboolib.platform.compat.PlaceholderExpansion
import java.text.DecimalFormat

object OriginPlaceholder : PlaceholderExpansion {

    init {
        BukkitPlugin.getInstance().logger.info("|- PlaceholderAPI plugin hooked.")
    }

    val df2 = DecimalFormat("0.00");

    override val identifier: String
        get() = "rpg"

    override fun onPlaceholderRequest(p: Player?, params: String): String {
        val split = params.split(":").toTypedArray()
        val key = split[0]
        val attributeData = OriginAttributeAPI.getAttributeData(p!!)

        if (split[0] == "combat-power") {
            val mode = split.getOrElse(1) { "d" }
            if (mode == "i") {
                return attributeData.combatPower.toInt().toString()
            }
            return attributeData.combatPower.toString()
        } else if (params == "health") {
            return df2.format(p.health)
        } else if (params == "max-health") {
            return df2.format(p.maxHealth)
        }

        val attribute = AttributeManager.usableAttributes.values.firstOrNull { it.toName() == key }


        if (attribute !== null) {

            val value = attribute.toValue(p, attributeData, split)
            return if (value is Double) {
                OriginAttribute.decimalFormat.format(value)
            } else {
                value.toString()
            }
        }
        return "N/O"
    }
}
