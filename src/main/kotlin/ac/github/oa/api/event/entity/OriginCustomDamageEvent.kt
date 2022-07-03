package ac.github.oa.api.event.entity

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.platform.type.BukkitProxyEvent

class OriginCustomDamageEvent(
    val damager: Entity,
    val entity: Entity,
    var damage: Double,
    val attacker: LivingEntity?,
    val origin: EntityDamageByEntityEvent?
) : BukkitProxyEvent() {
    val isProjectile: Boolean
        get() = damager is Projectile

    val cause = origin?.cause

}