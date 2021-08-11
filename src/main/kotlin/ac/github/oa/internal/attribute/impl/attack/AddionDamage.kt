package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.event.EventMemory
import ac.github.oa.internal.event.impl.DamageMemory

/**
 * 0 附加伤害
 */
class AddionDamage : SingleAttributeAdapter(AttributeType.ATTACK) {

    override val strings: Array<String>
        get() = arrayOf("附加攻击")
    override val type: ValueType
        get() = ValueType.NUMBER

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory) {
            val damageMemory: DamageMemory = eventMemory
            val baseDouble: BaseDouble = baseDoubles[0]
            damageMemory.addDamage(this, baseDouble.number())
        }
    }
}