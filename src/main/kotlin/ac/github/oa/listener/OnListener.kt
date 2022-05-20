package ac.github.oa.listener

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.api.event.entity.EntityDeathEvent
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.api.event.render.AttributeRenderStringEvent
import ac.github.oa.command.Command
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.impl.DamageMemory
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment

object OnListener {

    val remotes: List<String>
        get() = OriginAttribute.config.getStringList("options.remotes")

    @SubscribeEvent
    fun e(e: OriginCustomDamageEvent) {
        var damager: LivingEntity? = null
        var entity: LivingEntity? = null

        if (e.entity is LivingEntity) {
            entity = e.entity
        }

        if (e.damager is Projectile) {
            val projectile = e.damager
            val shooter = projectile.shooter
            if (shooter is LivingEntity) {
                damager = shooter
            }
        } else if (e.damager is LivingEntity) {
            damager = e.damager
        }

        if (damager != null && entity != null) {
            if (e.damager !is Projectile) {
                val itemStack = BukkitEquipment.HAND.getItem(damager)
                if (itemStack != null && itemStack.isNotAir() && itemStack.type.name in remotes) {
                    e.isCancelled = true
                    return
                }
            }

            val damageMemory = DamageMemory(
                damager,
                entity,
                e,
                OriginAttributeAPI.getAttributeData(damager),
                OriginAttributeAPI.getAttributeData(entity)
            )
            val entityDamageEvent = EntityDamageEvent(damageMemory, PriorityEnum.PRE)
            entityDamageEvent.call()
            e.isCancelled = entityDamageEvent.isCancelled
            if (!entityDamageEvent.isCancelled) {
                OriginAttributeAPI.callDamage(damageMemory)
                entityDamageEvent.priorityEnum = PriorityEnum.POST
                entityDamageEvent.call()

                // POST CALL
                if (!entityDamageEvent.isCancelled) {
                    e.damage = damageMemory.totalDamage.coerceAtLeast(1.0)
                    info("damage total ${e.damage}")
                }
            }
        }
    }

    val damageCauses: List<String>
        get() = OriginAttribute.config.getStringList("options.damage-cause")

    @SubscribeEvent(ignoreCancelled = false)
    fun e(e: EntityDamageByEntityEvent) {

        if (e.isCancelled) return

        val cause = e.cause
        if (damageCauses.contains(cause.name) || e.entity::class.java.simpleName != "PlayerNPC") {
            val entity = e.entity

            val event = OriginCustomDamageEvent(e.damager, entity, e.damage, e)
            event.call()
            e.isCancelled = event.isCancelled
            if (!event.isCancelled) {
                e.damage = event.damage

                if (entity is LivingEntity && entity.health - e.damage <= 0) {
                    EntityDeathEvent(entity, event).call()
                }
            }
        }

    }


    @SubscribeEvent
    fun e(event: ItemSpawnEvent) {
        if (event.isCancelled || event.entity.isCustomNameVisible) return
        val item = event.entity
        val itemStack = item.itemStack
        if (!event.isCancelled && itemStack.hasItemMeta() && itemStack.itemMeta!!.hasDisplayName()) {
            item.isCustomNameVisible = true
            if (itemStack.amount > 1) {
                item.customName = itemStack.itemMeta!!.displayName + " §b*" + itemStack.amount
            } else {
                item.customName = itemStack.itemMeta!!.displayName
            }
        }
    }

    @SubscribeEvent
    fun e(event: ItemMergeEvent) {
        if (event.isCancelled) return
        val item = event.target
        val oldItem = event.entity
        if (item.isCustomNameVisible) {
            item.customName =
                item.itemStack.itemMeta!!.displayName + " §b*" + (item.itemStack.amount + oldItem.itemStack.amount)
        }
    }

    @SubscribeEvent
    fun e(e: AttributeRenderStringEvent) {

        e.list.forEachIndexed { index, s ->
            var string = s
            val matchResult = OriginAttribute.numberRegex.find(s)
            matchResult?.apply {
                this.groupValues.forEach {
                    string =
                        s.replace(it, it.replace(OriginAttribute.config.getString("options.operator-add", "+")!!, "+"))
                    string =
                        s.replace(it, it.replace(OriginAttribute.config.getString("options.operator-take", "-")!!, "+"))
                }
            }
            e.list[index] = string
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

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent")
    fun handleMythicSpawn(ope: OptionalEvent) {
        val event = ope.get<MythicMobSpawnEvent>()
        val entity: Entity = event.entity

        if (entity is LivingEntity) {
            asyncUpdate(entity)
        }
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.EntityDeathEvent")
    fun handleMythicDeath(ope: OptionalEvent) {
        val event = ope.get<MythicMobSpawnEvent>()
        val entity: Entity = event.entity
        if (entity is LivingEntity) {
            OriginAttributeAPI.remove(entity.uniqueId)
        }
    }

    fun asyncUpdate(entity: LivingEntity) {
        OriginAttributeAPI.async {
            OriginAttributeAPI.loadEntityEquipment(entity)
            OriginAttributeAPI.callUpdate(entity)
        }
    }
}
