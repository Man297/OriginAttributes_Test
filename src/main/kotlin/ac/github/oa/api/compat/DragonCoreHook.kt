package ac.github.oa.api.compat

import ac.github.oa.OriginAttribute
import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.equip.AdaptItem
import ac.github.oa.internal.core.attribute.equip.Slot
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import eos.moe.dragoncore.database.IDataBase
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial

object DragonCoreHook {

    val isEnable by lazy { Bukkit.getPluginManager().isPluginEnabled("DragonCore") }

    val slots: List<String>
        get() = OriginAttribute.module.getStringList("dragon-core.slots")

    @SubscribeEvent(bind = "eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent")
    fun e(ope: OptionalEvent) {
        val e = ope.get<PlayerSlotUpdateEvent>()
        if (e.identifier in slots) {
            loadItem(e.player, e.identifier, e.itemStack)
            OriginAttributeAPI.callUpdate(e.player)
        }
    }

    fun loadItem(player: Player, identifier: String, itemStack: ItemStack) {
        val adaptItem = AdaptItem(DragonCoreSlot(identifier, itemStack))
        val attributeData = OriginAttributeAPI.loadItem(player, adaptItem)
        OriginAttributeAPI.setExtra(player.uniqueId, "dragon-core-${identifier}", attributeData)
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {

        if (isEnable) {
            SlotAPI.getAllSlotItem(e.player, object : IDataBase.Callback<MutableMap<String, ItemStack>> {
                override fun onResult(map: MutableMap<String, ItemStack>) {
                    map.forEach {
                        if (it.key in slots) {
                            loadItem(e.player, it.key, it.value)
                        }
                        XMaterial.DIAMOND_SWORD
                    }
                    OriginAttributeAPI.callUpdate(e.player)
                }

                override fun onFail() {

                }

            })
        }


    }

    class DragonCoreSlot(override val id: String, itemStack: ItemStack?) : Slot(itemStack) {

        override fun getItem(entity: LivingEntity): ItemStack {
            return item
        }
    }

}
