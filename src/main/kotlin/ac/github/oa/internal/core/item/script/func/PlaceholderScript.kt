package ac.github.oa.internal.core.item.script.func

import ac.github.oa.internal.core.item.script.BaseWrapper
import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object PlaceholderScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "papi"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val split = string.split("def:")
        val def = if (split.size == 2) split[1] else error("{papi:<expression>def:<default>} 缺少默认值")
        return if (entity is Player) PlaceholderAPI.setPlaceholders(entity, split[0]) else def
    }
}
