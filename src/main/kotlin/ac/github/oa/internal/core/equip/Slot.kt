package ac.github.oa.internal.core.equip

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.base.DataPair
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang

interface ISlot

abstract class Slot(
    private val itemStack: ItemStack?
) : ISlot, SlotCondition {
    val item: ItemStack
        get() = itemStack ?: ItemStack(Material.AIR)

    override fun screen(string: String, keyword: List<String>): Boolean {
        return keyword.any { string.contains(it) }
    }

}

interface SlotCondition {
    fun screen(string: String, keyword: List<String>): Boolean
}

interface SlotVariation {

    fun examine(livingEntity: LivingEntity, adaptItem: AdaptItem, patterns: List<String>): Boolean
}


class Hand(itemStack: ItemStack?) : Slot(itemStack)
class OffHand(itemStack: ItemStack?) : Slot(itemStack)
class Helmet(itemStack: ItemStack?) : Slot(itemStack)
class BreastPlate(itemStack: ItemStack?) : Slot(itemStack)
class Gaiter(itemStack: ItemStack?) : Slot(itemStack)
class Boot(itemStack: ItemStack?) : Slot(itemStack)
class InventorySlot(private val index: Int, itemStack: ItemStack?) : Slot(itemStack), SlotVariation {

    override fun examine(livingEntity: LivingEntity, adaptItem: AdaptItem, patterns: List<String>): Boolean {
        val orNull = patterns.firstOrNull { it.split(" ")[0].toInt() == index }
        if (orNull != null) {
            val split = orNull.split(" ")
            return if (!any(adaptItem.item.itemMeta?.lore ?: arrayListOf(), listOf(split[1]))) {
                livingEntity.sendLang("condition-slot-not-enough", item.itemMeta!!.displayName, split[1])
                false
            } else true
        }
        return false
    }

    fun any(list: List<String>, keyword: List<String>) = list.any { keyword.any { s -> it.contains(s) } }
}