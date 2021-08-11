package ac.github.oa.internal.event.impl

import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.event.EventMemory
import org.bukkit.entity.LivingEntity

class UpdateMemory(livingEntity: LivingEntity, attributeData: AttributeData) : EventMemory {
    var livingEntity: LivingEntity
    var attributeData: AttributeData

    init {
        this.livingEntity = livingEntity
        this.attributeData = attributeData
    }
}