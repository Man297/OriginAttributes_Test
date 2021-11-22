package ac.github.oa.internal.base.render

import ac.github.oa.OriginAttribute
import ac.github.oa.api.event.render.AttributeRenderStringEvent
import org.bukkit.ChatColor
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info

object RenderRuleHandler {

    val rules = mutableMapOf<String, IRenderRule>().also {
        it["regex"] = RegexRenderRule()
        it["prefix"] = PrefixRenderRule()
        it["suffix"] = SuffixRenderRule()
    }

    fun proof(string: String, targets: List<String> = OriginAttribute.skipStrings): Boolean {

        if (targets.isEmpty()) return false

        return targets.any {
            val split = it.split(" ")
            val key = split[0]
            rules[key]?.proof(string, it.replaceFirst(key, "").trim()) ?: false
        }
    }

    @SubscribeEvent
    fun e(e: AttributeRenderStringEvent) {
        e.list.removeIf { proof(ChatColor.stripColor(it)!!) }
    }
}
