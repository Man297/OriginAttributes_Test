package ac.github.oa.internal.core.equip

import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

interface ISlot

abstract class Slot(
    private val itemStack: ItemStack?
) : ISlot {
    val item: ItemStack
        get() = itemStack ?: buildItem(XMaterial.AIR)
}


class Hand(itemStack: ItemStack?) : Slot(itemStack)
class OffHand(itemStack: ItemStack?) : Slot(itemStack)
class Helmet(itemStack: ItemStack?) : Slot(itemStack)
class BreastPlate(itemStack: ItemStack?) : Slot(itemStack)
class Gaiter(itemStack: ItemStack?) : Slot(itemStack)
class Boot(itemStack: ItemStack?) : Slot(itemStack)
class InventorySlot(val index: Int, itemStack: ItemStack?) : Slot(itemStack)