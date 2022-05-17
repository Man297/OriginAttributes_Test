package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

class Damage : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.ATTACK)

    val physical = DefaultImpl()

    val magic = DefaultImpl()

    val addon = DefaultAddonImpl()

    val pvpDamage = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.injured is Player) {
                memory.addDamage(this, data.get(0))
            }
        }
    }

    val pveDamage = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.injured !is Player) {
                memory.addDamage(this, data.get(0))
            }
        }
    }

    class DefaultImpl : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.RANGE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            val damageMemory = memory as DamageMemory
            damageMemory.addDamage(name, data.get(this))
        }

    }

    class DefaultAddonImpl : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            memory.getDamageSources().forEach {
                if (it.value > 0) {
                    it.value += (it.value * data.get(0))
                }
            }
        }

    }
}
