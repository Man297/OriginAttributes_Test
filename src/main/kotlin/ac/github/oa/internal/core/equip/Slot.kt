package ac.github.oa.internal.core.equip

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

abstract class Slot(private val itemStack: ItemStack?) : ISlot, SlotCondition {
    val item: ItemStack
        get() = itemStack ?: ItemStack(Material.AIR)

    override fun screen(string: String, keyword: List<String>): Boolean {
        return keyword.any { string.contains(it) }
    }
}