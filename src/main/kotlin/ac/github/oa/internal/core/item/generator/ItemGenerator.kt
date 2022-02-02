package ac.github.oa.internal.core.item.generator

import ac.github.oa.internal.core.item.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

interface ItemGenerator {

    val name: String

    fun build(entity: LivingEntity?, item: Item, map: MutableMap<String, String>): ItemStack

}