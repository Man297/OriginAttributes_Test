package ac.github.oa.internal.attribute.impl.attack

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeData

import ac.github.oa.internal.attribute.AttributeType
import ac.github.oa.internal.attribute.abst.SingleAttributeAdapter
import ac.github.oa.internal.base.BaseDouble
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.enums.ValueType
import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.base.event.impl.DamageMemory
import taboolib.common.platform.event.SubscribeEvent

class UltimateHarm : SingleAttributeAdapter(AttributeType.ATTACK) {
    override fun method(eventMemory: EventMemory, baseDoubles: Array<BaseDouble>) {}

    @SubscribeEvent
    fun e(e: EntityDamageEvent) {
        if (e.priorityEnum == PriorityEnum.POST) {
            val damageMemory: DamageMemory = e.damageMemory
            val attributeData: AttributeData = OriginAttributeAPI.getAttributeData(damageMemory.attacker)
            val baseDoubles: Array<BaseDouble> = attributeData.find(UltimateHarm::class.java)
            val number: Double = baseDoubles[0].globalEval(damageMemory.damage)
            damageMemory.addDamage(this, number)
        }
    }

    override val strings: Array<String>
        get() = arrayOf("最终伤害")
    override val type: ValueType
        get() = ValueType.ALL
}