package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import ac.github.oa.util.sync
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class Health : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.UPDATE)


    val health = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        val default: Double
            get() = root.getDouble("${this.name}.default")

        val isHealthScale: Boolean
            get() = healthScale != -1.0

        val healthScale: Double
            get() = root.getDouble("${this.name}.health-scale", -1.0)

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as UpdateMemory
            val livingEntity = memory.livingEntity

            val percent = memory.attributeData.getData(this@Health.index, percent.index).get(percent)
            val result = (data.get(this) + if (livingEntity is Player) default else 0.0) * (1 + percent)

            sync {
                livingEntity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)?.apply {
                    AttributeModifier(
                        livingEntity.uniqueId,
                        "OriginAttribute",
                        result,
                        AttributeModifier.Operation.ADD_NUMBER
                    ).also {
                        removeModifier(it)
                        addModifier(it)
                    }

                }
                if (isHealthScale && livingEntity is Player) {
                    livingEntity.isHealthScaled = true
                    livingEntity.healthScale = healthScale
                }
            }
        }

        override fun toValue(entity: LivingEntity, args: String, data: AttributeData.Data): Any? {
            return when (args) {
                "max" -> OriginAttribute.decimalFormat.format(entity.maxHealth)
                else -> OriginAttribute.decimalFormat.format(entity.health)
            }
        }

    }

    val percent = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {}
    }

}
