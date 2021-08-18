package ac.github.oa.internal.core.hook

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.base.BaseDouble
import org.bukkit.entity.Player
import taboolib.platform.BukkitPlugin
import taboolib.platform.compat.PlaceholderExpansion
import java.util.*

object OriginPlaceholder : PlaceholderExpansion {

    init {
        BukkitPlugin.getInstance().logger.info("|- PlaceholderAPI plugin hooked.")
    }

    override val identifier: String
        get() = "rpg"

    override fun onPlaceholderRequest(p: Player, params: String): String {
        val split = params.split(":").toTypedArray()
        val key = split[0]
        val args = if (split.size == 1) "" else split[1]
        val attributeData: AttributeData = OriginAttributeAPI.getAttributeData(p)
        val optional: Optional<AttributeAdapter> = AttributeManager.getAttributeAdapter(key)
        if (optional.isPresent) {
            val attributeAdapter: AttributeAdapter = optional.get()
            val baseDoubles: Array<BaseDouble> = attributeData.find(attributeAdapter)
            attributeAdapter.format(p, args, baseDoubles)?.apply {
                return if (this is Double) {
                    OriginAttribute.decimalFormat.format(this)
                } else this.toString()
            }
        }
        return "N/O"
    }
}