package ac.github.oa.internal.core.equip

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

interface ISlot {

    val id: String


    fun getItem(entity: LivingEntity): ItemStack?


}