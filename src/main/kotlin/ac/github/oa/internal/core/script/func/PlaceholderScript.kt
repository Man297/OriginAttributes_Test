package ac.github.oa.internal.core.script.func

import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

@Awake
class PlaceholderScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "papi"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        val split = string.split(":")
        val def = if (split.size == 2) split[1] else "papi undefined"
        return if (entity is Player) PlaceholderAPI.setPlaceholders(entity, split[0]) else def
    }
}