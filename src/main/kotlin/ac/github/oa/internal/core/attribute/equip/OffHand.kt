package ac.github.oa.internal.core.attribute.equip

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.type.BukkitEquipment

class OffHand(itemStack: ItemStack?) : Slot(itemStack) {


    override val id: String
        get() = BukkitEquipment.OFF_HAND.name

    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.OFF_HAND.getItem(entity)
    }
}