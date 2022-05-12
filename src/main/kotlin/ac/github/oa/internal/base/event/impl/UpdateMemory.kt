package ac.github.oa.internal.base.event.impl

import ac.github.oa.internal.base.event.EventMemory
import ac.github.oa.internal.core.attribute.AttributeData
import org.bukkit.entity.LivingEntity

class UpdateMemory(livingEntity: LivingEntity, attributeData: AttributeData) : EventMemory {
    var livingEntity: LivingEntity
    var attributeData: AttributeData

    init {
        this.livingEntity = livingEntity
        this.attributeData = attributeData
    }
}
