package ac.github.oa.internal.attribute.impl.defense

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory

class Dodge : SingleAttributeAdapter(AttributeType.DEFENSE) {
    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            if (baseDoubles[0].random()) {
                damageMemory.setLabel(Dodge::class.java, true)
                damageMemory.event.setCancelled(true)
            }
        }
    }

    override val strings: Array<String>
        get() = arrayOf("闪避概率")
    override val type: ValueType
        get() = ValueType.PERCENT
}