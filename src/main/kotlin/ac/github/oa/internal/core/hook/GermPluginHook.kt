package ac.github.oa.internal.core.hook

import ac.github.oa.api.event.entity.EntityLoadEquipmentEvent
import org.bukkit.Bukkit
import taboolib.common.platform.event.SubscribeEvent

object GermPluginHook {

    val isEnable: Boolean
        get() = Bukkit.getPluginManager().isPluginEnabled("GermPlugin")

    @SubscribeEvent
    fun e(e : EntityLoadEquipmentEvent) {

    }

}
