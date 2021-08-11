package ac.github.oa.internal.attribute.impl.update

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.event.EventMemory
import ac.github.oa.internal.event.impl.UpdateMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.submit

class MoveSpeed : SingleAttributeAdapter(AttributeType.OTHER) {
    override fun defaultOption(config: BaseConfig) {
        super.defaultOption(config)
        config.select(this)["base"] = 0.2
    }

    val baseValue: Double
        get() = baseConfig.select(this).any("base").asNumber().toDouble()

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is UpdateMemory) {
            val updateMemory: UpdateMemory = eventMemory
            val livingEntity: LivingEntity = updateMemory.livingEntity
            if (livingEntity is Player) {
                val player: Player = livingEntity
                val defaultValue = baseValue
                val result: Double = defaultValue + defaultValue * (baseDoubles[0].number() / 100)
                if (player.getWalkSpeed().toDouble() == result) return
                submit(async = false) {
                    player.walkSpeed = result.toFloat()
                }
            }
        }
    }

    override val strings: Array<String>
        get() = arrayOf("移速加成")
    override val type: ValueType
        get() = ValueType.NUMBER
}