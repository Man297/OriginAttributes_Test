package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType

class Damage : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.ATTACK)

    val physical = DamageDefault()

    val magic = DamageDefault()

    class DamageDefault : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.RANGE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            val damageMemory = memory as DamageMemory
            damageMemory.addDamage(this, data.get(this))
        }

    }
}
