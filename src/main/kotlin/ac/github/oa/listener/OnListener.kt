package ac.github.oa.listener

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.projectiles.ProjectileSource
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.SubscribeEvent


@Awake(LifeCycle.ENABLE)
object OnListener {


    @SubscribeEvent
    fun e(e: EntityDamageByEntityEvent) {
        var damager: LivingEntity? = null
        var entity: LivingEntity? = null
        if (e.damager is LivingEntity && e.entity is LivingEntity) {
            entity = e.entity as LivingEntity
            if (e.damager is Projectile) {
                val projectile: Projectile = e.damager as Projectile
                val shooter: ProjectileSource = projectile.shooter!!
                if (shooter is LivingEntity) {
                    damager = shooter
                }
            } else if (e.damager is LivingEntity) {
                damager = e.damager as LivingEntity
            }
        }
        if (damager != null && entity != null) {
            e.damage = 0.0
            val a: AttributeData = OriginAttributeAPI.getAttributeData(damager)
            val d: AttributeData = OriginAttributeAPI.getAttributeData(entity)
            val damageMemory = DamageMemory(damager, entity, e, a, d)
            val entityDamageEvent = EntityDamageEvent(damageMemory, PriorityEnum.PRE)
            entityDamageEvent.call()
            if (!entityDamageEvent.isCancelled) {
                OriginAttributeAPI.callDamage(damageMemory)
                entityDamageEvent.priorityEnum = PriorityEnum.POST
                entityDamageEvent.call()

                // POST CALL
                if (!entityDamageEvent.isCancelled) {
                    e.damage = damageMemory.damage.coerceAtLeast(1.0)
                }
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        asyncUpdate(e.player)
    }

    @SubscribeEvent
    fun e(e: EntityPickupItemEvent) {
        asyncUpdate(e.entity)
    }

    @SubscribeEvent
    fun e(e: PlayerDropItemEvent) {
        asyncUpdate(e.player)
    }

    @SubscribeEvent
    fun e(e: PlayerItemHeldEvent) {
        asyncUpdate(e.player)
    }

    @SubscribeEvent
    fun e(e: InventoryCloseEvent) {
        asyncUpdate(e.player)
    }

    //
//    @SubscribeEvent
//    fun onItemSpawnEvent(event: ItemSpawnEvent) {
//        if (event.isCancelled() || !OriginAttribute.instance.getConfig().getBoolean("options.item-display-name") ||
//            event.getEntity().isCustomNameVisible()
//        ) return
//        val item: Item = event.getEntity()
//        val itemStack = item.itemStack
//        if (!event.isCancelled() && itemStack.hasItemMeta() && itemStack.itemMeta!!.hasDisplayName()) {
//            item.isCustomNameVisible = true
//            if (itemStack.amount > 1) {
//                item.customName = itemStack.itemMeta!!.displayName + " §b*" + itemStack.amount
//            } else {
//                item.customName = itemStack.itemMeta!!.displayName
//            }
//        }
//    }

    fun asyncUpdate(entity: LivingEntity) {
        OriginAttributeAPI.async {
            OriginAttributeAPI.loadEntityEquipment(entity)
            OriginAttributeAPI.callUpdate(entity)
        }
    }
}