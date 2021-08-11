package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.api.event.entity.EntityDamageEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.attribute.impl.defense.Armor
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.event.EventMemory
import ac.github.oa.internal.event.impl.DamageMemory
import taboolib.common.platform.SubscribeEvent

/**
 * 破甲攻击
 */
class ArmorBreak : SingleAttributeAdapter(AttributeType.ATTACK), Listener {
    override val strings: Array<String>
        get() = arrayOf("破甲几率")
    override val type: ValueType
        get() = ValueType.PERCENT

    @SubscribeEvent
    fun e(e: EntityDamageEvent) {
        if (e.priorityEnum == PriorityEnum.POST) {
            val damageMemory: DamageMemory = e.damageMemory
            if (damageMemory.labels.containsKey(ArmorBreak::class.java) && damageMemory.labels.containsKey(Armor::class.java)) {
                val result = damageMemory.labels.get(Armor::class.java) as Double
                damageMemory.addDamage("armor-break-damage", -(result * 0.5))
            }
        }
    }

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            val baseDouble: BaseDouble = baseDoubles[0]
            if (baseDouble.random()) {
                damageMemory.setLabel(ArmorBreak::class.java, true)
            }
        }
    }
}