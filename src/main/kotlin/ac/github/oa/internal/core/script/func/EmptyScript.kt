package ac.github.oa.internal.core.script.func

import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import org.bukkit.entity.Entity
import taboolib.common.platform.Awake

@Awake
class EmptyScript : InternalScript<BaseWrapper> {

    companion object {
        const val NAMESPACE = "%del%"
    }
    override val name: String
        get() = "empty"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        return NAMESPACE
    }

}
