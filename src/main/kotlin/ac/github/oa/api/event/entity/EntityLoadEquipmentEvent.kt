package ac.github.oa.api.event.entity

import ac.github.oa.internal.core.attribute.equip.AdaptItem
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class EntityLoadEquipmentEvent(
    val livingEntity: LivingEntity,
    val list: MutableList<AdaptItem>
) : BukkitProxyEvent()