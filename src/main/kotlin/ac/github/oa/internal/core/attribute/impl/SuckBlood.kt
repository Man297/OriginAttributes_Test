package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.*
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.util.random
import taboolib.common5.Coerce
import kotlin.math.max
import kotlin.math.min

class SuckBlood : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.ATTACK)

    companion object {


        @SubscribeEvent(ignoreCancelled = true)
        fun e(e: EntityDamageEvent) {
            val damageMemory = e.damageMemory
            if (e.priorityEnum == PriorityEnum.POST && damageMemory.labels["@SuckBlood"] == true) {
                val result = Coerce.toDouble(damageMemory.labels["suck-blood"])
                if (result > 0) {
                    damageMemory.attacker.health =
                        min(damageMemory.attacker.health + result, damageMemory.attacker.maxHealth)
                }
            }
        }


    }

    val chance = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            if (memory is DamageMemory) {
                memory.setLabel("@SuckBlood", random(data.get(this)))
            }
        }
    }

    val ratio = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as DamageMemory
            if (memory.labels["@SuckBlood"] == true) {
                val value = data.get(this)
                memory.labels["suck-blood"] = memory.totalDamage * value / 100
            }
        }

    }

}
