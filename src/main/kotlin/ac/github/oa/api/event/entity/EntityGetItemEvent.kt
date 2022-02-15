package ac.github.oa.api.event.entity

import ac.github.oa.internal.core.item.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class EntityGetItemEvent(val entity: LivingEntity, val result: MutableList<ItemStack>, val item: Item) :
    BukkitProxyEvent() {
}