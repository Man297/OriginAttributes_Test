package ac.github.oa.internal.core.item

import ac.github.oa.api.ItemAPI
import ac.github.oa.api.event.item.ItemUpdateEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object ItemUpdater {

    @Schedule(period = 20 * 60 * 30, async = true)
    fun tick() {
        Bukkit.getOnlinePlayers().forEach { player -> ItemAPI.checkUpdate(player, player.inventory) }
    }

    /**
     * 保留物品数量
     */
    @SubscribeEvent
    fun e(e: ItemUpdateEvent) {
        e.newItemStack.amount = e.oldItemStack.amount
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        ItemAPI.checkUpdate(e.player, e.player.inventory)
    }

    @SubscribeEvent
    fun e(e: PlayerRespawnEvent) {
        ItemAPI.checkUpdate(e.player, e.player.inventory)
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: InventoryOpenEvent) {
        kotlin.runCatching {
            if (e.inventory.location != null) {
                submit(async = true) {
                    ItemAPI.checkUpdate(e.player as Player, e.inventory)
                }
            }
        }
    }
}
