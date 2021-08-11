package ac.github.oa.internal.item.impl

import ac.github.oa.internal.item.ItemGenerator
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

class DefaultGenerator : ItemGenerator {

    override val name: String
        get() = ""

    override fun build(entity: LivingEntity?, config: ConfigurationSection): ItemStack {


        return buildItem(XMaterial.AIR)
    }


}