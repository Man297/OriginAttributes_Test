package ac.github.oa.api.event.entity

import ac.github.oa.internal.attribute.AttributeData
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.ProxyEvent

class EntityGetterDataEvent(
    val livingEntity: LivingEntity,
    val attributeData: AttributeData
) : ProxyEvent() {
}