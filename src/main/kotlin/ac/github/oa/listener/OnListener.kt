package ac.github.oa.listener

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.api.event.entity.EntityDeathEvent
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.api.event.render.AttributeRenderStringEvent
import ac.github.oa.command.Command
import ac.github.oa.internal.attribute.AttributeData
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.impl.DamageMemory
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.util.isNotAir
import taboolib.platform.util.sendMessage
import taboolib.type.BukkitEquipment


object OnListener {

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
                if (itemStack != null && itemStack.isNotAir() && OriginAttribute.config.getStringList("options.remotes")
                        .any { it == itemStack.type.name }
                ) {
                    e.isCancelled = true
                    return
                }
            }

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
                    if (damager is Player && Command.isDebugEnable(damager)) {
                        (0..10).forEach { _ -> damager.sendMessage(" ") }
                        damager.sendMessage("You damage ${entity.name}: Damage logs.")
                        damageMemory.labels.onEachIndexed { index, entry ->
                            damager.sendMessage("${index}.${entry.key} = ${entry.value}")
                        }
                        damager.sendMessage("Total amount ${e.damage}")
                        damager.sendMessage("Is cancel ${e.isCancelled}")
                    }

                    if (entity is Player && Command.isDebugEnable(entity)) {
                        (0..10).forEach { _ -> entity.sendMessage(" ") }
                        entity.sendMessage("${damager.name} damage Your: Damage logs.")
                        damageMemory.labels.onEachIndexed { index, entry ->
                            entity.sendMessage("${index}.${entry.key} = ${entry.value}")
                        }
                        entity.sendMessage("Total amount ${e.damage}")
                        entity.sendMessage("Is cancel ${e.isCancelled}")
                    }

                }
            }
        }
    }

    val damageCauses: List<String>
        get() = OriginAttribute.config.getStringList("options.damage-cause")

    @SubscribeEvent
    fun e(e: EntityDamageByEntityEvent) {

        val cause = e.cause
        if (damageCauses.contains(cause.name)) {
            val entity = e.entity
            val event = OriginCustomDamageEvent(e.damager, entity, e.damage, e)
            event.call()
            if (!event.isCancelled) {
                e.damage = event.damage

                if (entity is LivingEntity && entity.health - e.damage <= 0) {
                    EntityDeathEvent(entity, event).call()
                }

            }
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
                        s.replace(it, it.replace(OriginAttribute.config.getString("options.operator-add", "+"), "+"))
                    string =
                        s.replace(it, it.replace(OriginAttribute.config.getString("options.operator-take", "-"), "+"))
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