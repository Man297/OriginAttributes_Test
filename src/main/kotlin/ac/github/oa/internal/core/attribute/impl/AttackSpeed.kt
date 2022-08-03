package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeType
import ac.github.oa.util.sync

import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


class AttackSpeed : AbstractAttribute() {


    companion object {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun e(e: EntityDamageEvent) {
            // 修正无来源伤害
            if (e.damageMemory.attacker is Player) {
                e.damageMemory.damage = e.damageMemory.damage * e.damageMemory.vigor
                // 修正有来源伤害 统一修正

                e.damageMemory.getDamageSources().forEach {
                    it.value = it.value * e.damageMemory.vigor
                }
            }

        }

    }

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.UPDATE)

    val default: Double
        get() = toRoot().getDouble("${toName()}.default", 4.0)

    val scale: Double
        get() = toRoot().getDouble("${toName()}.scale", 1.0)

    val value = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as UpdateMemory
            val livingEntity = memory.livingEntity
            val ratio = memory.attributeData.getData(this@AttackSpeed.index, ratio.index).get(ratio)
            val result = default + data.get(this) * (1 + ratio) * scale
            sync {
                livingEntity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED)?.baseValue = result
            }
        }
    }

    val ratio = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {}
    }

}
