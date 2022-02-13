package ac.github.oa.internal.core.equip

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.type.BukkitEquipment

class Boot(itemStack: ItemStack?) : Slot(itemStack) {
    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.FEET.getItem(entity)
    }
}