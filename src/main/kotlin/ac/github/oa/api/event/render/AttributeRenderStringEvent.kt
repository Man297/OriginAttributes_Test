package ac.github.oa.api.event.render

import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class AttributeRenderStringEvent(val livingEntity: LivingEntity, val list: MutableList<String>) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = true
}