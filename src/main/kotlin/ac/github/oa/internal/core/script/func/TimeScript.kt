package ac.github.oa.internal.core.script.func

import ac.github.oa.OriginAttribute
import ac.github.oa.internal.core.script.BaseWrapper
import ac.github.oa.internal.core.script.InternalConfig
import ac.github.oa.internal.core.script.InternalScript
import org.bukkit.entity.Entity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

@Awake(LifeCycle.ENABLE)
class TimeScript : InternalScript<BaseWrapper> {
    override val name: String
        get() = "time"

    override fun execute(entity: Entity?, wrapper: BaseWrapper, config: InternalConfig, string: String): String? {
        return OriginAttribute.simpleDateFormat.format(string.toLong())
    }
}