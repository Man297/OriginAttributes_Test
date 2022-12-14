package ac.github.oa.internal.core.attribute.equip

import ac.github.oa.internal.core.item.ItemInstance
import ac.github.oa.internal.core.item.ItemPlant
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

class AdaptItem(val slot: Slot, var enable: Boolean = false) {

    val item: ItemStack
        get() = slot.item

    val isValid: Boolean
        get() = item.isNotAir() && item.hasItemMeta()

    fun instance(): ItemInstance? {
        return ItemInstance.get(item)
    }

    override fun toString(): String {
        return "AdaptItem(slot=$slot, enable=$enable)"
    }


}
