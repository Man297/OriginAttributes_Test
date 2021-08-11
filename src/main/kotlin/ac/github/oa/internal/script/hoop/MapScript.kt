package ac.github.oa.internal.script.hoop

import ac.github.oa.internal.script.BaseWrapper
import ac.github.oa.internal.script.InternalScript
import org.bukkit.entity.Entity

class MapScript : InternalScript<MapScript.Wrapper> {

    class Wrapper : HashMap<String, Any>(), BaseWrapper

    override val name: String
        get() = "map"

    override fun execute(entity: Entity?, wrapper: Wrapper, string: String): String? {
        return wrapper[string].toString()
    }

}