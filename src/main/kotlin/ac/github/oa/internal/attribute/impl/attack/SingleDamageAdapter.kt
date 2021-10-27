package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory

abstract class SingleDamageAdapter : SingleAttributeAdapter(AttributeType.ATTACK) {

    abstract fun doScreen(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>): Boolean

    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {
        if (eventMemory is DamageMemory && doScreen(eventMemory, baseDoubles)) {
            val number: Double = baseDoubles[0].globalEval(eventMemory.damage)
            eventMemory.addDamage(this, number)
        }
    }

}