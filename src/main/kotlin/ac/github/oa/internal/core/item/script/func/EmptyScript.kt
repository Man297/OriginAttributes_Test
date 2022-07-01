package ac.github.oa.internal.core.item.script.func

import ac.github.oa.internal.core.item.script.BaseWrapper
import ac.github.oa.internal.core.item.script.InternalConfig
import ac.github.oa.internal.core.item.script.InternalScript
import org.bukkit.entity.Entity

object EmptyScript : InternalScript<BaseWrapper> {

    const val NAMESPACE = "%del%"

    override val name: String
        get() = "empty"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        return NAMESPACE
    }

}
