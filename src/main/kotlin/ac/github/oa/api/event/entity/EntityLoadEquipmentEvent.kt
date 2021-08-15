package ac.github.oa.api.event.entity

import ac.github.oa.internal.core.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.ProxyEvent

class EntityLoadEquipmentEvent(
    val livingEntity: LivingEntity,
    val list: List<AdaptItem>
) : ProxyEvent()