package ac.github.oa.api.compat

import ac.github.oa.internal.core.item.ItemPlant
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import jdk.internal.dynalink.beans.StaticClass
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.util.random
import taboolib.common5.Coerce
import taboolib.type.BukkitEquipment

object MythicMobsHook {


    // MythicMob 5
    @SubscribeEvent(bind = "io.lumine.mythic.bukkit.events.MythicMobDeathEvent")
    fun e0(ope: OptionalEvent) {
        val event = ope.get<io.lumine.mythic.bukkit.events.MythicMobDeathEvent>()
        val mm = event.mobType

        event.drops.addAll(
            handleDrop(
                mm.config.key,
                mm.config.getStringList("OriginOptions.Drops")
            )
        )
    }

    fun handleDrop(displayName: String, drops: List<String>): MutableList<ItemStack> {
        val itemStacks = mutableListOf<ItemStack>()
        drops.filter { it.contains(" ") }.forEach { str ->
            val args = str.split(" ")
            var amount = 1
            if (args.size > 2 && args[2].isNotEmpty() && !random(Coerce.toDouble(args[2]))) {
                return@forEach
            }
            if (args.size > 1 && args[1].isNotEmpty()) { // 数量判断
                if (args[1].contains("-") && args[1].split("-".toRegex()).toTypedArray().size > 1) {
                    val i1 =
                        args[1].split("-".toRegex()).toTypedArray()[0].replace("[^\\d]".toRegex(), "").toInt()
                    val i2 =
                        args[1].split("-".toRegex()).toTypedArray()[1].replace("[^\\d]".toRegex(), "").toInt()
                    if (i1 > i2) {
                        info("MythicMobs - Drop Random Error: " + displayName + " - " + str)
                    } else {
                        amount = random().nextInt(i2 - i1 + 1) + i1
                    }
                } else {
                    amount = args[1].replace("[^\\d]".toRegex(), "").toInt()
                }
            }
            repeat((0 until amount).count()) {
                ItemPlant.build(null, args[0])?.let {
                    itemStacks += it
                } ?: info("MythicMobs - Drop No Item: " + displayName + " - " + args[0])
            }
        }
        return itemStacks

    }


    // MythicMob 4
    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent")
    fun e(ope: OptionalEvent) {
        val event = ope.get<MythicMobDeathEvent>()
        val mm = event.mobType
        event.drops.addAll(
            handleDrop(
                mm.config.key,
                mm.config.getStringList("OriginOptions.Drops")
            )
        )
    }

    fun handleUpdate(entity: LivingEntity, equipments: List<String>) {
        equipments.forEach {
            val split = it.split(" ")
            val key = split[0].uppercase()
            val slot = split[1]
            if (ItemPlant.hasKey(key)) {
                BukkitEquipment.valueOf(slot).setItem(entity, ItemPlant.build(entity, key) ?: return@forEach)
            }
        }
    }

    @SubscribeEvent(bind = "io.lumine.mythic.bukkit.events.MythicMobSpawnEvent")
    fun e2(ope: OptionalEvent) {
        val event = ope.get<io.lumine.mythic.bukkit.events.MythicMobSpawnEvent>()
        val mobType = event.mobType
        val entity = event.entity as LivingEntity

        handleUpdate(entity, mobType.config.getStringList("OriginOptions.Equipment"))
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent")
    fun e1(ope: OptionalEvent) {
        val event = ope.get<MythicMobSpawnEvent>()
        val mobType = event.mobType
        val entity = event.entity as LivingEntity
        handleUpdate(entity, mobType.config.getStringList("OriginOptions.Equipment"))
    }
}