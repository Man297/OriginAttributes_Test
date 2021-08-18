package ac.github.oa.internal.core.script.hoop

import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Entity
import org.jetbrains.annotations.NotNull
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

@Awake
class MapScript : InternalScript<MapScript.Wrapper> {

    class Wrapper : HashMap<String, Any>(), BaseWrapper

    override val name: String
        get() = "map"

    override fun execute(entity: Entity?, wrapper: Wrapper, config: InternalConfig, string: String): String {
        return wrapper[string].toString()
    }


}