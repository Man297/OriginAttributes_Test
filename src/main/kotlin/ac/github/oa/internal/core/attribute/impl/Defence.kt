package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.*
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common.reflect.Reflex.Companion.getProperty

class Defence : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.DEFENSE)

    val physical = DefaultImpl()

    val magic = DefaultImpl()

    val addon = DefaultAddonImpl()

    val pvpDefence = object : AbstractDefenceEntry() {
        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.attacker is Player) {
                memory.addDamage(this, -(data.get(0) * scale))
            }
        }
    }

    val pveDefence = object : AbstractDefenceEntry() {
        override val type: Attribute.Type
            get() = Attribute.Type.RANGE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.attacker !is Player) {
                memory.addDamage(this, -(data.get(0) * scale))
            }
        }
    }


    class DefaultImpl : AbstractDefenceEntry() {

        override val type: Attribute.Type
            get() = Attribute.Type.RANGE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            memory.getDamageSources().forEach {
                if (it.any is Damage.DefaultImpl) {
                    if (it.any.name == name) {
                        it.value -= data.get(this) * scale
                    }
                }
            }
        }

    }

    class DefaultAddonImpl : AbstractDefenceEntry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            memory.getDamageSources().forEach {
                if (it.any is Damage.DefaultImpl) {
                    it.value -= (it.value * data.get(this) / 100) * scale
                }
            }
        }

    }

    abstract class AbstractDefenceEntry : Attribute.Entry() {

        val scale: Double
            get() = node.toRoot().getDouble("${name}.scale", 1.0)

    }


}
