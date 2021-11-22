package ac.github.oa.internal.base.render

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.render.AttributeRenderStringEvent
import org.bukkit.ChatColor
import taboolib.common.platform.event.SubscribeEvent


interface IRenderRule {

    fun proof(string: String, target: String): Boolean

}

class PrefixRenderRule : IRenderRule {
    override fun proof(string: String, target: String): Boolean {
        return string.startsWith(target)
    }
}

class SuffixRenderRule : IRenderRule {
    override fun proof(string: String, target: String): Boolean {
        return string.endsWith(target)
    }
}

class RegexRenderRule : IRenderRule {
    override fun proof(string: String, target: String): Boolean {
        return string.matches(target.toRegex())
    }

}