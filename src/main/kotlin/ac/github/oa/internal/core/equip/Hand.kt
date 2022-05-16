package ac.github.oa.internal.core.equip

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.type.BukkitEquipment

class Hand(itemStack: ItemStack?) : Slot(itemStack) {


    override val id: String
        get() = BukkitEquipment.HAND.name


    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.HAND.getItem(entity)
    }
}