package ac.github.oa.command

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.plugin.AttributeMapRenderEvent
import ac.github.oa.internal.core.attribute.AttributeManager
import ac.github.oa.internal.core.attribute.impl.Map
import ac.github.oa.util.ArrayUtils
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

object CommandMap {

    val mapInstance: Map
        get() = AttributeManager.getAttribute("Map") as Map

}