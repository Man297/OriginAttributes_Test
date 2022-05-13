package ac.github.oa.internal.core.script.content

import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.internal.core.script.hoop.MapScript
import org.bukkit.entity.Entity
import taboolib.common.platform.Awake

@Awake
class IndexScript : InternalScript<MapScript.Wrapper> {
    override val name: String
        get() = "i"

    override fun execute(entity: Entity?, wrapper: MapScript.Wrapper, config: InternalConfig, string: String): String? {
        return wrapper["value"].toString()
    }

}