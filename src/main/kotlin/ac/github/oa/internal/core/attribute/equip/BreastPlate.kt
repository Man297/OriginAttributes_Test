package ac.github.oa.internal.core.attribute.equip

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.type.BukkitEquipment


class BreastPlate(itemStack: ItemStack?) : Slot(itemStack) {


    override val id: String
        get() = BukkitEquipment.CHEST.name


    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.CHEST.getItem(entity)
    }
}