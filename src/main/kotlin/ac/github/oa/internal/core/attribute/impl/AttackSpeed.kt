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
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


class AttackSpeed : AbstractAttribute() {


    companion object {

        @SubscribeEvent(priority = EventPriority.MONITOR)
        fun e(e: EntityDamageEvent) {
            // 修正无来源伤害
            e.damageMemory.damage = e.damageMemory.damage * e.damageMemory.accumulatorPower
            // 修正有来源伤害 统一修正
            e.damageMemory.getDamageSources().forEach {
                it.value = it.value * e.damageMemory.accumulatorPower
            }
        }

    }

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.UPDATE)

    val value = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as UpdateMemory
            val livingEntity = memory.livingEntity
            val ratio = memory.attributeData.getData(this@AttackSpeed.index, ratio.index).get(ratio)
            val result = data.get(this) * (1 + ratio)
            sync {
                if (!OriginAttribute.original) {
                    livingEntity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED)?.baseValue = 0.0
                }
                livingEntity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED)?.apply {
                    AttributeModifier(
                        livingEntity.uniqueId, "OriginAttribute", result, AttributeModifier.Operation.ADD_NUMBER
                    ).also {
                        removeModifier(it)
                        addModifier(it)
                    }
                }
            }
        }
    }
    val ratio = object : Attribute.Entry() {
        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {}
    }

}
