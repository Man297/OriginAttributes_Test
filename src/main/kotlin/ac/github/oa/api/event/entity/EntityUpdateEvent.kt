package ac.github.oa.api.event.entity

import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.core.attribute.AttributeData
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent


class EntityUpdateEvent(
    var entity: LivingEntity,
    var attributeData: AttributeData,
    var priorityEnum: PriorityEnum = PriorityEnum.PRE
) : BukkitProxyEvent()
