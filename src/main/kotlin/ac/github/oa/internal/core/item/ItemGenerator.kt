package ac.github.oa.internal.core.item

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection

interface ItemGenerator {

    val name: String

    fun build(entity: LivingEntity?, config: ConfigurationSection): ItemStack

}