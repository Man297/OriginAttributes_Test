package ac.github.oa.api.event.entity

import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class EntityDeathEvent(val entity: LivingEntity,val cause : OriginCustomDamageEvent) : BukkitProxyEvent() {
}