package ac.github.oa.internal.core.attribute.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeType
import taboolib.common.platform.function.info
import taboolib.common.util.random

class Crit : AbstractAttribute() {
    override val types: Array<AttributeType>
        get() = arrayOf(AttributeType.ATTACK)


    val change = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            if (memory is DamageMemory) {

                // 暴击抵抗
                val critChanceResistance =
                    memory.injuredAttributeData
                        .getData(this@Crit.index, critChanceResistance.index)
                        .get(critChanceResistance)

                memory.setLabel(this, random((data.get(this) - critChanceResistance) / 100))
            }
        }
    }

    val damage = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {
            if (memory is DamageMemory && memory.labels[change] == true) {

                // 爆伤抵抗
                val critChanceResistance =
                    memory.injuredAttributeData
                        .getData(this@Crit.index, critDamageResistance.index)
                        .get(critDamageResistance)
                val scope = (data.get(0) - critChanceResistance) / 100

                // 给伤害来源先乘以倍数
                memory.getDamageSources().forEach {
                    it.value += it.value * scope
                }
            }
        }

    }

    val critChanceResistance = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {}

    }


    val critDamageResistance = object : Attribute.Entry() {

        override val type: Attribute.Type
            get() = Attribute.Type.SINGLE

        override fun handler(memory: EventMemory, data: AttributeData.Data) {}

    }


}
