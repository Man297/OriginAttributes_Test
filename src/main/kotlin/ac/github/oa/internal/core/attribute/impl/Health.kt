package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import kotlin.math.max

class Health : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.UPDATE)


    val health = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        val default: Double
            get() = root.getDouble("${this.name}.default")

        val isHealthScale: Boolean
            get() = root.isDouble("${this.name}.health-scale")

        val healthScale: Double
            get() = root.getDouble("${this.name}.health-scale")

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as UpdateMemory
            val livingEntity = memory.livingEntity
            // 点数 + 百分百
            // 110 + 40 * 1 + 0
            val percent = memory.attributeData.getData(this@Health.index, percent.index).get(percent)
            val result = (data.get(this) + default) * (1 + percent)
            livingEntity.maxHealth = result

            if (livingEntity.health >= result) {
                livingEntity.health = result
            }

            if (isHealthScale && livingEntity is Player) {
                livingEntity.isHealthScaled = true
                livingEntity.healthScale = healthScale
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
