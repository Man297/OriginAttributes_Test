package ac.github.oa.internal.core.attribute.equip

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.sendLang

class InventorySlot(private val index: Int, itemStack: ItemStack?) : Slot(itemStack), SlotVariation {

    override val id: String
        get() = index.toString()

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