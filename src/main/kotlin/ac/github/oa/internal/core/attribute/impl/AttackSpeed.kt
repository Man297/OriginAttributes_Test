package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.UpdateMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeType
import ac.github.oa.util.sync

import org.bukkit.attribute.AttributeModifier


class AttackSpeed : AbstractAttribute() {

    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.UPDATE)


    val health = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE


        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            memory as UpdateMemory
            val livingEntity = memory.livingEntity

            val result = data.get(this)

            sync {
                livingEntity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED)?.apply {
                    AttributeModifier(
                        livingEntity.uniqueId,
                        "OriginAttribute",
                        result,
                        AttributeModifier.Operation.ADD_NUMBER
                    ).also {
                        removeModifier(it)
                        addModifier(it)
                    }
                }
            }
        }
    }


}
