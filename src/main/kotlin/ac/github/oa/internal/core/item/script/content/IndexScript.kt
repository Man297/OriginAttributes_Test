package ac.github.oa.internal.core.item.script.content

import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import ac.github.oa.internal.core.item.script.hoop.MapScript
import org.bukkit.entity.Entity

object IndexScript : InternalScript<MapScript.Wrapper> {
    override val name: String
        get() = "i"

    override fun execute(entity: Entity?, wrapper: MapScript.Wrapper, config: InternalConfig, string: String): String? {
        return wrapper["value"].toString()
    }

}