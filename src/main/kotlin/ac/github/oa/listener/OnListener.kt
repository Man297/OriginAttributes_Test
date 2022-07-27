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
import ac.github.oa.internal.core.attribute.AttributeData
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment

object OnListener {

    val remotes: List<String>
        get() = OriginAttribute.config.getStringList("options.remotes")

    val damageCauses: List<String>
        get() = OriginAttribute.config.getStringList("options.damage-cause")


    @SubscribeEvent
    fun e(e: OriginCustomDamageEvent) {
        val entity: LivingEntity = e.entity as? LivingEntity ?: return
        val attacker = e.attacker ?: return


        if (!e.isProjectile) {
            val itemStack = BukkitEquipment.HAND.getItem(attacker)
            if (itemStack != null && itemStack.isNotAir() && itemStack.type.name in remotes) {
                e.isCancelled = true
                return
            }
        }

        val attr = if (e.isProjectile && e.damager.hasMetadata("attributeData")) {
            e.damager.getMetadata("attributeData").first().value() as AttributeData
        } else {
            OriginAttributeAPI.getAttributeData(attacker)
        }
        val damageMemory = DamageMemory(
            attacker, entity, e, attr, OriginAttributeAPI.getAttributeData(entity)
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
                e.damage = damageMemory.totalDamage.coerceAtLeast(0.0)
            }
        }

    }


    @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun e(e: EntityDamageByEntityEvent) {
        if (e.isCancelled) return

        val cause = e.cause
        if (cause.name in damageCauses || e.entity::class.java.simpleName != "PlayerNPC") {
            val entity = e.entity

            val damager = e.damager
            var attacker: LivingEntity? = null

            if (damager is LivingEntity) {
                attacker = damager
            } else if (damager is Projectile) {
                attacker = damager.shooter as? LivingEntity
            }


            val event = OriginCustomDamageEvent(damager, entity, e.damage, attacker, e)
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

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        event.projectile.setMetadata(
            "attributeData",
            FixedMetadataValue(BukkitPlugin.getInstance(), OriginAttributeAPI.getAttributeData(event.entity))
        )
        event.projectile.setMetadata(
            "force", FixedMetadataValue(BukkitPlugin.getInstance(), event.force)
        )
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
            asyncUpdate(entity, 15)
        }
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.EntityDeathEvent")
    fun handleMythicDeath(ope: OptionalEvent) {
        val event = ope.get<EntityDeathEvent>()
        val entity: Entity = event.entity
        if (entity is LivingEntity) {
            OriginAttributeAPI.remove(entity.uniqueId)
        }
    }

    fun asyncUpdate(entity: LivingEntity, delay: Long = 0) {
        OriginAttributeAPI.async(delay = delay) {
            OriginAttributeAPI.loadEntityEquipment(entity)
            OriginAttributeAPI.callUpdate(entity)
        }
    }
}
