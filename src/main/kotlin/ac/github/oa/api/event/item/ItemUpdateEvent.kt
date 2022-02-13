package ac.github.oa.api.event.item

import ac.github.oa.internal.core.item.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class ItemUpdateEvent(
    val player: Player,
    val oldItemStack: ItemStack,
    var newItemStack: ItemStack,
    var item: Item
) : BukkitProxyEvent() {
}