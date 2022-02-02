package ac.github.oa.internal.core.equip

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.sendLang
import taboolib.type.BukkitEquipment

interface ISlot {

    fun getItem(entity: LivingEntity): ItemStack?
}

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


class Hand(itemStack: ItemStack?) : Slot(itemStack) {
    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.HAND.getItem(entity)
    }
}

class OffHand(itemStack: ItemStack?) : Slot(itemStack) {
    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.OFF_HAND.getItem(entity)
    }
}

class Helmet(itemStack: ItemStack?) : Slot(itemStack) {
    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.HEAD.getItem(entity)
    }
}

class BreastPlate(itemStack: ItemStack?) : Slot(itemStack) {
    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.CHEST.getItem(entity)
    }
}

class Gaiter(itemStack: ItemStack?) : Slot(itemStack) {
    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.LEGS.getItem(entity)
    }
}

class Boot(itemStack: ItemStack?) : Slot(itemStack) {
    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.FEET.getItem(entity)
    }
}

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

    override fun getItem(entity: LivingEntity): ItemStack? {
        return when {
            entity is Player -> entity.inventory.getItem(index)
            else -> XMaterial.AIR.parseItem()
        }
    }

    fun any(list: List<String>, keyword: List<String>) = list.any { keyword.any { s -> it.contains(s) } }
}