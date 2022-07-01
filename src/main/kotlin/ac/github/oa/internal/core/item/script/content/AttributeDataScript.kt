package ac.github.oa.internal.core.item.script.content

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeManager
import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import ac.github.oa.internal.core.item.script.hoop.MapScript
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

object AttributeDataScript : InternalScript<MapScript.Wrapper> {
    override val name: String
        get() = "data"

    // {data:MoveSpeed:ratio}
    override fun execute(entity: Entity?, wrapper: MapScript.Wrapper, config: InternalConfig, string: String): String? {
        if (entity !is LivingEntity || !wrapper.containsKey("data")) return "0.0"
        val attributeData = wrapper["data"] as AttributeData

        val split = string.split(":").toTypedArray()
        val key = split[0]

        val attribute = AttributeManager.usableAttributes.values.firstOrNull { it.toName() == key }
        if (attribute !== null) {
            val entry = attribute.getEntry(split[1])

            val data = attributeData.getData(attribute.getPriority(), entry.index)

            entry.toValue(entity, if (split.size == 3) split[2] else "", data)?.apply {
                return if (this is Double) {
                    OriginAttribute.decimalFormat.format(this)
                } else this.toString()
            }
        }
        return "0.00"
    }

}