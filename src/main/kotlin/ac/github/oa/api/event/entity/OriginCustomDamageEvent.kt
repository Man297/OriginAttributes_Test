package ac.github.oa.api.event.entity

import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.platform.type.BukkitProxyEvent

class OriginCustomDamageEvent(
    val damager: Entity,
    val entity: Entity,
    var damage: Double,
    val origin: EntityDamageByEntityEvent?
) : BukkitProxyEvent()