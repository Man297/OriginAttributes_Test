package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.*
import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.getProperty

class Defence : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.DEFENSE)

    val physical = DefaultImpl()

    val magic = DefaultImpl()

    val addon = DefaultAddonImpl()

    val pvpDefence = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.injured is Player) {
                memory.addDamage(this, -data.get(0))
            }
        }
    }

    val pveDefence = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.injured !is Player) {
                memory.addDamage(this, -data.get(0))
            }
        }
    }


    class DefaultImpl : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.RANGE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            memory.getDamageSources().forEach {
                if (it.any is Damage.DefaultImpl) {
                    if (it.any.name == name) {
                        it.value -= data.get(this)
                    }
                }
            }
        }

    }

    class DefaultAddonImpl : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            memory.getDamageSources().forEach {
                if (it.any is Damage.DefaultImpl) {
                    it.value -= it.value * data.get(this) / 100
                }
            }
        }

    }


}