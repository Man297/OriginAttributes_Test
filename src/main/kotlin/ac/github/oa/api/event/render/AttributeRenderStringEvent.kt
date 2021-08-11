package ac.github.oa.api.event.render

import org.bukkit.entity.LivingEntity
import taboolib.common.platform.ProxyEvent

class AttributeRenderStringEvent(val livingEntity: LivingEntity, val list: List<String>) : ProxyEvent() {

    override val allowCancelled: Boolean
        get() = true
}