package ac.github.oa.api.compat

import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.internal.core.item.script.hoop.MapScript
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.util.random
import taboolib.type.BukkitEquipment

object MythicMobsHook {

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent")
    fun e(ope: OptionalEvent) {
        val event = ope.get<MythicMobDeathEvent>()
        if (event.killer is Player) {
            val mm = event.mobType
            val drops = event.drops
            val dropLockKiller = mm.config.getBoolean("OriginOptions.DropLockKiller", false)

            for (str in mm.config.getStringList("OriginOptions.Drops")) {
                if (str.contains(" ")) {
                    val args = str.split(" ")
                    var amount = 1
                    if (args.size > 2 && args[2].isNotEmpty() && random()
                            .nextDouble() > args[2].replace("[^0-9.]".toRegex(), "").toDouble()
                    ) {
                        continue
                    }
                    if (args.size > 1 && args[1].isNotEmpty()) { // 数量判断
                        if (args[1].contains("-") && args[1].split("-".toRegex()).toTypedArray().size > 1) {
                            val i1 =
                                args[1].split("-".toRegex()).toTypedArray()[0].replace("[^\\d]".toRegex(), "").toInt()
                            val i2 =
                                args[1].split("-".toRegex()).toTypedArray()[1].replace("[^\\d]".toRegex(), "").toInt()
                            if (i1 > i2) {
                                info("MythicMobs - Drop Random Error: " + mm.displayName + " - " + str)
                            } else {
                                amount = random().nextInt(i2 - i1 + 1) + i1
                            }
                        } else {
                            amount = args[1].replace("[^\\d]".toRegex(), "").toInt()
                        }
                    }
                    repeat((0 until amount).count()) {
                        ItemPlant.build(if (dropLockKiller) event.killer else null, args[0])?.let {
                            drops.add(it)
                        } ?: info("MythicMobs - Drop No Item: " + mm.displayName + " - " + args[0])
                    }
                }
            }
        }
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent")
    fun e1(ope: OptionalEvent) {
        val event = ope.get<MythicMobSpawnEvent>()
        val mobType = event.mobType
        val wrapper = MapScript.Wrapper()
        val entity = event.entity as LivingEntity
        // 覆盖背包物品
        mobType.config.getStringList("OriginOptions.Equipment")?.forEach {
            val split = it.split(" ")
            val key = split[0]
            val slot = split[1]
            if (ItemPlant.hasKey(key)) {
                ItemPlant.build(entity, key)?.let {
                    BukkitEquipment.valueOf(slot).setItem(entity, it)
                }
            } else {
                info("MythicMobs - Drop No Item: " + event.mobType.displayName + " - " + key)
            }
        }
    }
}