package ac.github.oa.internal.core.equip

import org.bukkit.inventory.ItemStack

class AdaptItem(val slot: Slot, var enable: Boolean = false) {

    val item: ItemStack
        get() = slot.item

}