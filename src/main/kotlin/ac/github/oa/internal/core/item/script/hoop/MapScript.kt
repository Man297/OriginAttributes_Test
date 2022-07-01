package ac.github.oa.internal.core.item.script.hoop

import ac.github.oa.internal.core.item.script.BaseWrapper
import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import org.bukkit.entity.Entity

object MapScript : InternalScript<MapScript.Wrapper> {

    class Wrapper : HashMap<String, Any>(), BaseWrapper

    override val name: String
        get() = "map"

    override fun execute(entity: Entity?, wrapper: Wrapper, config: InternalConfig, string: String): String {
        return wrapper[string].toString()
    }


}