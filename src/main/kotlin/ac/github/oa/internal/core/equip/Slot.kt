package ac.github.oa.internal.core.equip

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.base.DataPair
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

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


class Hand(itemStack: ItemStack?) : Slot(itemStack)
class OffHand(itemStack: ItemStack?) : Slot(itemStack)
class Helmet(itemStack: ItemStack?) : Slot(itemStack)
class BreastPlate(itemStack: ItemStack?) : Slot(itemStack)
class Gaiter(itemStack: ItemStack?) : Slot(itemStack)
class Boot(itemStack: ItemStack?) : Slot(itemStack)
class InventorySlot(val index: Int, itemStack: ItemStack?) : Slot(itemStack) {
    override fun screen(string: String, keyword: List<String>): Boolean {
        val map = keyword.map {
            val split = it.split(' ')
            DataPair(split[0], split[1])
        }
        val dataPair = map.find { it.key.toInt() == index }
        if (dataPair != null) {
            return string.contains(dataPair.key)
        }
        return true
    }
}