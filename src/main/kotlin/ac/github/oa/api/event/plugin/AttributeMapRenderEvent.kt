package ac.github.oa.api.event.plugin

import ac.github.oa.internal.core.attribute.impl.Map
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class AttributeMapRenderEvent(
    val entity: LivingEntity,
    val list: MutableList<String>,
    val entry: Map.DefaultImpl
) : BukkitProxyEvent()