package ac.github.oa.internal.core.hook

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.EntityLoadEquipmentEvent
import ac.github.oa.internal.core.equip.AdaptItem
import ac.github.oa.internal.core.equip.Slot
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent

object DragonCoreHook {

    val isEnable: Boolean
        get() = Bukkit.getPluginManager().isPluginEnabled("GermPlugin")

    val slots: List<String>
        get() = OriginAttribute.module.getStringList("dragon-core.slots")

    @SubscribeEvent
    fun e(e: PlayerSlotUpdateEvent) {
        if (e.identifier in slots) {

            val adaptItem = AdaptItem(DragonCoreSlot(e.identifier, e.itemStack))
            val attributeData = OriginAttributeAPI.loadItem(e.player, adaptItem)

            OriginAttributeAPI.setExtra(e.player.uniqueId, "dragon-core-${e.identifier}", attributeData)
            OriginAttributeAPI.callUpdate(e.player)
        }
    }

    class DragonCoreSlot(val id: String, itemStack: ItemStack?) : Slot(itemStack) {

        override fun getItem(entity: LivingEntity): ItemStack {
            return item
        }
    }

}
