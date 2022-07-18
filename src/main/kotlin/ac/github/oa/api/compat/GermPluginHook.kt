package ac.github.oa.api.compat

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.entity.EntityLoadEquipmentEvent
import ac.github.oa.internal.core.attribute.equip.AdaptItem
import ac.github.oa.internal.core.attribute.equip.Slot
import com.germ.germplugin.api.GermSlotAPI
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent

object GermPluginHook {

    val isEnable by lazy { Bukkit.getPluginManager().isPluginEnabled("GermPlugin") }

    val slots: List<String>
        get() = OriginAttribute.module.getStringList("germ-plugin.slots")

    @SubscribeEvent
    fun e(e: EntityLoadEquipmentEvent) {
        if (!isEnable) return
        val player = e.livingEntity as? Player ?: return
        e.list += slots.map { AdaptItem(GermPluginSlot(it, GermSlotAPI.getItemStackFromIdentity(player, it))) }
    }

    class GermPluginSlot(override val id: String, itemStack: ItemStack?) : Slot(itemStack) {

        override fun getItem(entity: LivingEntity): ItemStack {
            return item
        }
    }

}
