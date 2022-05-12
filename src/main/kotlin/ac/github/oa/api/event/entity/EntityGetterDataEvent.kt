package ac.github.oa.api.event.entity

import ac.github.oa.internal.core.attribute.AttributeData
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class EntityGetterDataEvent(
    val livingEntity: LivingEntity,
    val attributeData: AttributeData
) : BukkitProxyEvent() {
}
