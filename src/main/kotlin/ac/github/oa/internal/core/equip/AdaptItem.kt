package ac.github.oa.internal.core.equip

import ac.github.oa.internal.core.item.Item
import ac.github.oa.internal.core.item.ItemInstance
import ac.github.oa.internal.core.item.ItemPlant
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

class AdaptItem(val slot: Slot, var enable: Boolean = false) {

    val item: ItemStack
        get() = slot.item

    fun instance(): ItemInstance? {
        if (item.isAir()) return null
        val parseItem = ItemPlant.parseItem(item) ?: return null
        return ItemInstance(item, parseItem, slot)
    }

}