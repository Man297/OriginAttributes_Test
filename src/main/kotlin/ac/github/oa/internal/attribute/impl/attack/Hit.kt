package ac.github.oa.internal.attribute.impl.attack


import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeData

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseConfig
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.event.EventMemory
import ac.github.oa.internal.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.SubscribeEvent

/**
 * 0 命中几率
 */
class Hit : SingleAttributeAdapter(AttributeType.ATTACK) {
    override fun defaultOption(config: BaseConfig) {
        super.defaultOption(config)
        config.select(this)
            .set("default", 100)
    }

    override val strings: Array<String>
        get() = arrayOf("命中几率")

    override fun format(entity: LivingEntity, s: String, baseDoubles: Array<BaseDouble>): Any {
        return baseDoubles[0].number(defaultValue)
    }

    val defaultValue: Double
        get() = baseConfig.select(this).any("default").asNumber().toDouble()

    @SubscribeEvent
    fun e(e: EntityDamageEvent) {
        if (e.priorityEnum == PriorityEnum.POST) {
            val damageMemory: DamageMemory = e.damageMemory
            val attackAttributeData: AttributeData = damageMemory.attackAttributeData
            val baseDoubles: Array<BaseDouble> = attackAttributeData.find(Hit::class.java)
            if (!baseDoubles[0].random(defaultValue)) {
                e.isCancelled = true
            }
        }
    }

    override val type: ValueType
        get() = ValueType.PERCENT

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}
}