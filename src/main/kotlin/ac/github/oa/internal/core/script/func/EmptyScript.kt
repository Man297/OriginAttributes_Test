package ac.github.oa.internal.core.script.func

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.util.ReportUtil
import org.bukkit.entity.Entity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.BukkitPlugin
import java.util.logging.Level

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
