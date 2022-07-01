package ac.github.oa.internal.core.attribute.equip

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.type.BukkitEquipment

class Helmet(itemStack: ItemStack?) : Slot(itemStack) {


    override val id: String
        get() = BukkitEquipment.HEAD.name


    override fun getItem(entity: LivingEntity): ItemStack? {
        return BukkitEquipment.HEAD.getItem(entity)
    }
}