package ac.github.oa.api.event.item

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class ItemDurabilityDamageEvent(
    val player: Player,
    val itemStack: ItemStack,
    val maxDurability: Int,
    val oldDurability: Int,
    val durability: Int,
) : BukkitProxyEvent() {

}