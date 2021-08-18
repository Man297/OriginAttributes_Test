package ac.github.oa.internal.attribute.impl.update

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

class MoveSpeed : SingleAttributeAdapter(AttributeType.UPDATE) {
    override fun defaultOption(config: BaseConfig) {
        super.defaultOption(config)
        config.select(this)["base"] = 0.2
    }

    private val baseValue: Double
        get() = baseConfig.select(this).any("base").asNumber().toDouble()

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is UpdateMemory) {
            val updateMemory: UpdateMemory = eventMemory
            val livingEntity: LivingEntity = updateMemory.livingEntity
            if (livingEntity is Player) {
                val player: Player = livingEntity
                val defaultValue = baseValue
                val result: Double = defaultValue + defaultValue * (baseDoubles[0].percent(ValueType.NUMBER))
                if (player.walkSpeed.toDouble() == result) return
                submit(async = false) {
                    player.walkSpeed = result.toFloat().coerceAtMost(1f)
                }
            }
        }
    }

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return baseDoubles[0].value(ValueType.PERCENT)
    }

    override val strings: Array<String>
        get() = arrayOf("移速加成")
    override val type: ValueType
        get() = ValueType.PERCENT
}